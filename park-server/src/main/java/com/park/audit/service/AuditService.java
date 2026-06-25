package com.park.audit.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.park.audit.dto.AuditDTO;
import com.park.audit.entity.AuditRecord;
import com.park.audit.mapper.AuditMapper;
import com.park.auth.entity.SysUser;
import com.park.auth.mapper.UserMapper;
import com.park.common.exception.BusinessException;
import com.park.common.result.ResultCode;
import com.park.evaluation.entity.EvaluationRecord;
import com.park.evaluation.mapper.EvaluationMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

/**
 * 审核服务
 * 状态定义：0=草稿, 1=待区县审, 2=待市局审, 3=通过, 4=驳回
 * 审核动作：1=通过, 2=驳回
 *
 * @author park-team
 */
@Slf4j
@Service
public class AuditService {

    @Autowired
    private AuditMapper auditMapper;

    @Autowired
    private EvaluationMapper evaluationMapper;

    @Autowired
    private UserMapper userMapper;

    /**
     * 查询审核记录分页列表（待审核 + 已审核）
     *
     * @param pageNum    页码
     * @param pageSize   每页数量
     * @param status     状态筛选（可选）
     * @param roleType   角色类型：2=区县初审, 1=市级终审
     * @return 分页结果
     */
    public IPage<EvaluationRecord> getAuditPage(Integer pageNum, Integer pageSize,
                                                  String status, Integer roleType) {
        Page<EvaluationRecord> page = new Page<>(pageNum, pageSize);
        LambdaQueryWrapper<EvaluationRecord> wrapper = new LambdaQueryWrapper<>();

        // 根据角色类型确定查询的状态范围
        if (roleType == 2) {
            // 区县审核：查看 1=待区县审 状态的记录
            if ("pending".equals(status)) {
                wrapper.eq(EvaluationRecord::getStatus, 1);
            } else if ("audited".equals(status)) {
                wrapper.in(EvaluationRecord::getStatus, Arrays.asList(3, 4));
            } else {
                // 查看所有区县相关的记录
                wrapper.in(EvaluationRecord::getStatus, Arrays.asList(1, 3, 4));
            }
        } else if (roleType == 1) {
            // 市级审核：查看 2=待市局审 状态的记录
            if ("pending".equals(status)) {
                wrapper.eq(EvaluationRecord::getStatus, 2);
            } else if ("audited".equals(status)) {
                wrapper.in(EvaluationRecord::getStatus, Arrays.asList(3, 4));
            } else {
                // 查看所有市级相关的记录
                wrapper.in(EvaluationRecord::getStatus, Arrays.asList(2, 3, 4));
            }
        }

        wrapper.orderByDesc(EvaluationRecord::getCreateTime);
        return evaluationMapper.selectPage(page, wrapper);
    }

    /**
     * 使用外部构建的查询条件进行分页查询（供Controller扩展查询使用）
     *
     * @param page    分页对象
     * @param wrapper 查询条件
     * @return 分页结果
     */
    public IPage<EvaluationRecord> getAuditPageWithWrapper(Page<EvaluationRecord> page,
                                                             LambdaQueryWrapper<EvaluationRecord> wrapper) {
        return evaluationMapper.selectPage(page, wrapper);
    }

    /**
     * 查询待审核列表
     *
     * @param pageNum  页码
     * @param pageSize 每页数量
     * @param roleType 角色类型
     * @return 分页结果
     */
    public IPage<EvaluationRecord> getPendingAuditPage(Integer pageNum, Integer pageSize,
                                                        Integer roleType) {
        return getAuditPage(pageNum, pageSize, "pending", roleType);
    }

    /**
     * 查询已审核列表
     *
     * @param pageNum  页码
     * @param pageSize 每页数量
     * @param roleType 角色类型
     * @return 分页结果
     */
    public IPage<EvaluationRecord> getAuditedPage(Integer pageNum, Integer pageSize,
                                                    Integer roleType) {
        return getAuditPage(pageNum, pageSize, "audited", roleType);
    }

    /**
     * 执行审核操作
     *
     * @param auditDTO  审核请求参数
     * @param auditorId 审核人ID
     * @param roleType  审核人角色类型（2=区县管理员, 1=市级管理员）
     */
    @Transactional(rollbackFor = Exception.class)
    public void audit(AuditDTO auditDTO, Long auditorId, Integer roleType) {
        // 1. 参数校验
        if (auditDTO.getAction() != 1 && auditDTO.getAction() != 2) {
            throw new BusinessException(ResultCode.PARAM_ERROR, "审核动作必须为 1(通过) 或 2(驳回)");
        }

        // 2. 查询评价记录
        EvaluationRecord evaluation = evaluationMapper.selectById(auditDTO.getEvaluationId());
        if (evaluation == null) {
            throw new BusinessException(ResultCode.DATA_NOT_FOUND, "评价记录不存在");
        }

        // 3. 根据角色类型和当前状态确定审核级别并校验状态
        Integer newStatus;

        if (roleType == 2) {
            // 区县管理员 - 初审
            if (evaluation.getStatus() != 1) {
                throw new BusinessException(ResultCode.FAILURE,
                        "当前状态不允许区县审核，期望状态：1(待区县审)，实际状态：" + evaluation.getStatus());
            }
            if (auditDTO.getAction() == 1) {
                newStatus = 2; // 2=待市局审
            } else {
                newStatus = 4; // 4=驳回
            }
        } else if (roleType == 1) {
            // 市级管理员 - 终审
            if (evaluation.getStatus() != 2) {
                throw new BusinessException(ResultCode.FAILURE,
                        "当前状态不允许市级审核，期望状态：2(待市局审)，实际状态：" + evaluation.getStatus());
            }
            if (auditDTO.getAction() == 1) {
                newStatus = 3; // 3=通过
            } else {
                newStatus = 4; // 4=驳回
            }
        } else {
            throw new BusinessException(ResultCode.FORBIDDEN, "无审核权限");
        }

        // 4. 更新评价记录状态
        evaluation.setStatus(newStatus);
        evaluationMapper.updateById(evaluation);
        log.info("评价记录审核完成：evaluationId={}, newStatus={}, auditorId={}",
                auditDTO.getEvaluationId(), newStatus, auditorId);

        // 5. 获取审核人信息
        SysUser auditor = userMapper.selectById(auditorId);

        // 6. 创建审核记录
        AuditRecord auditRecord = new AuditRecord();
        auditRecord.setEvaluationId(auditDTO.getEvaluationId());
        auditRecord.setAuditorId(auditorId);
        auditRecord.setAuditorName(auditor != null ? auditor.getRealName() : null);
        auditRecord.setAuditorRole(roleType);
        auditRecord.setAction(auditDTO.getAction());
        auditRecord.setOpinion(auditDTO.getOpinion());
        auditRecord.setCreateTime(LocalDateTime.now());
        auditMapper.insert(auditRecord);
        log.info("审核记录创建成功：id={}", auditRecord.getId());
    }

    /**
     * 查询某条评价记录的审核历史
     *
     * @param evaluationId 评价记录ID
     * @return 审核记录列表
     */
    public List<AuditRecord> getAuditHistory(Long evaluationId) {
        LambdaQueryWrapper<AuditRecord> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(AuditRecord::getEvaluationId, evaluationId);
        wrapper.orderByDesc(AuditRecord::getCreateTime);
        return auditMapper.selectList(wrapper);
    }
}
