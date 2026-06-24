package com.park.audit.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.park.audit.dto.AuditDTO;
import com.park.audit.dto.AuditListItemDTO;
import com.park.audit.entity.AuditRecord;
import com.park.audit.mapper.AuditMapper;
import com.park.auth.entity.SysUser;
import com.park.auth.mapper.UserMapper;
import com.park.common.exception.BusinessException;
import com.park.common.result.ResultCode;
import com.park.evaluation.entity.EvaluationRecord;
import com.park.evaluation.mapper.EvaluationMapper;
import com.park.park.entity.ParkInfo;
import com.park.park.mapper.ParkMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

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
    private ParkMapper parkMapper;

    @Autowired
    private UserMapper userMapper;

    /**
     * 查询审核记录分页列表（待审核 + 已审核）
     *
     * @param pageNum       页码
     * @param pageSize      每页数量
     * @param status        状态筛选（可选）
     * @param roleType      角色类型：2=区县初审, 1=市级终审
     * @param districtName  区县名称（区县管理员时使用）
     * @return 分页结果
     */
    public IPage<EvaluationRecord> getAuditPage(Integer pageNum, Integer pageSize,
                                                  String status, Integer roleType, String districtName) {
        Page<EvaluationRecord> page = new Page<>(pageNum, pageSize);
        LambdaQueryWrapper<EvaluationRecord> wrapper = new LambdaQueryWrapper<>();

        applyStatusFilter(wrapper, status, roleType);
        
        // 区县管理员：只显示本区县的园区审核记录
        if (roleType == 2 && districtName != null && !districtName.isEmpty()) {
            wrapper.inSql(EvaluationRecord::getParkId, 
                "SELECT id FROM park_info WHERE district_name = '" + districtName + "'");
        }

        wrapper.orderByDesc(EvaluationRecord::getCreateTime);
        return evaluationMapper.selectPage(page, wrapper);
    }

    /**
     * 查询审核记录分页列表（带园区信息）
     *
     * @param pageNum          页码
     * @param pageSize         每页数量
     * @param status           状态筛选（可选）
     * @param roleType         角色类型：2=区县初审, 1=市级终审
     * @param districtName     区县名称（区县管理员时使用）
     * @param parkName         园区名称（可选）
     * @param parkType         园区类型（可选）
     * @param evaluationStatus 参评状态（可选）
     * @param evaluationYear   评价年份（可选）
     * @return 审核列表项DTO列表
     */
    public List<AuditListItemDTO> getAuditListWithParkInfo(Integer pageNum, Integer pageSize,
                                                           String status, Integer roleType, String districtName,
                                                           String parkName, String parkType,
                                                           Integer evaluationStatus, Integer evaluationYear) {
        // 先查询评价记录
        Page<EvaluationRecord> page = new Page<>(pageNum, pageSize);
        LambdaQueryWrapper<EvaluationRecord> wrapper = new LambdaQueryWrapper<>();

        // 根据角色类型和状态筛选确定查询的状态范围
        applyStatusFilter(wrapper, status, roleType);
        
        // 获取需要筛选的园区ID列表
        List<Long> parkIds = new ArrayList<>();
        boolean hasParkFilter = false; // 标记是否有园区筛选条件
        
        // 区县管理员：只显示本区县的园区审核记录
        if (roleType == 2 && districtName != null && !districtName.isEmpty()) {
            hasParkFilter = true;
            LambdaQueryWrapper<ParkInfo> districtWrapper = new LambdaQueryWrapper<>();
            districtWrapper.eq(ParkInfo::getDistrictName, districtName);
            districtWrapper.select(ParkInfo::getId);
            parkIds.addAll(parkMapper.selectObjs(districtWrapper).stream()
                .map(o -> ((Number) o).longValue()).collect(Collectors.toList()));
        }

        // 园区名称筛选
        if (parkName != null && !parkName.isEmpty()) {
            hasParkFilter = true;
            LambdaQueryWrapper<ParkInfo> nameWrapper = new LambdaQueryWrapper<>();
            nameWrapper.like(ParkInfo::getParkName, parkName);
            nameWrapper.select(ParkInfo::getId);
            List<Long> nameParkIds = parkMapper.selectObjs(nameWrapper).stream()
                .map(o -> ((Number) o).longValue()).collect(Collectors.toList());
            if (parkIds.isEmpty()) {
                parkIds.addAll(nameParkIds);
            } else {
                parkIds.retainAll(nameParkIds);
            }
        }

        // 园区类型筛选
        if (parkType != null && !parkType.isEmpty()) {
            hasParkFilter = true;
            LambdaQueryWrapper<ParkInfo> typeWrapper = new LambdaQueryWrapper<>();
            typeWrapper.eq(ParkInfo::getParkType, Integer.parseInt(parkType));
            typeWrapper.select(ParkInfo::getId);
            List<Long> typeParkIds = parkMapper.selectObjs(typeWrapper).stream()
                .map(o -> ((Number) o).longValue()).collect(Collectors.toList());
            if (parkIds.isEmpty()) {
                parkIds.addAll(typeParkIds);
            } else {
                parkIds.retainAll(typeParkIds);
            }
        }
        
        // 如果有园区筛选条件但没有匹配的园区，直接返回空结果
        if (hasParkFilter && parkIds.isEmpty()) {
            return new ArrayList<>();
        }
        
        // 如果有园区ID筛选条件
        if (!parkIds.isEmpty()) {
            wrapper.in(EvaluationRecord::getParkId, parkIds);
        }

        // 参评状态筛选
        if (evaluationStatus != null) {
            wrapper.eq(EvaluationRecord::getEvalStatus, evaluationStatus);
        }

        // 评价年份筛选
        if (evaluationYear != null) {
            wrapper.eq(EvaluationRecord::getEvalYear, evaluationYear);
        }

        wrapper.orderByDesc(EvaluationRecord::getCreateTime);
        IPage<EvaluationRecord> evaluationPage = evaluationMapper.selectPage(page, wrapper);

        // 转换为DTO并关联园区信息
        List<AuditListItemDTO> result = new ArrayList<>();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

        for (EvaluationRecord record : evaluationPage.getRecords()) {
            AuditListItemDTO dto = new AuditListItemDTO();
            dto.setId(record.getId());
            dto.setParkId(record.getParkId());
            dto.setAuditStatus(record.getStatus());
            dto.setEvaluationStatus(record.getEvalStatus() != null ? record.getEvalStatus() : 1); // 参评状态独立字段
            dto.setEvaluationYear(record.getEvalYear());
            dto.setCreateTime(record.getCreateTime() != null ? record.getCreateTime().format(formatter) : null);

            // 查询园区信息
            ParkInfo park = parkMapper.selectById(record.getParkId());
            if (park != null) {
                dto.setParkName(park.getParkName());
                dto.setDistrictName(park.getDistrictName());
                dto.setParkType(park.getParkType());
            }

            result.add(dto);
        }

        return result;
    }

    /**
     * 查询审核记录总数
     *
     * @param status           状态筛选（可选）
     * @param roleType         角色类型
     * @param districtName     区县名称（区县管理员时使用）
     * @param parkName         园区名称（可选）
     * @param parkType         园区类型（可选）
     * @param evaluationStatus 参评状态（可选）
     * @param evaluationYear   评价年份（可选）
     * @return 总数
     */
    public Long getAuditCount(String status, Integer roleType, String districtName,
                              String parkName, String parkType,
                              Integer evaluationStatus, Integer evaluationYear) {
        LambdaQueryWrapper<EvaluationRecord> wrapper = new LambdaQueryWrapper<>();

        applyStatusFilter(wrapper, status, roleType);
        
        // 获取需要筛选的园区ID列表
        List<Long> parkIds = new ArrayList<>();
        boolean hasParkFilter = false; // 标记是否有园区筛选条件
        
        // 区县管理员：只统计本区县的园区审核记录
        if (roleType == 2 && districtName != null && !districtName.isEmpty()) {
            hasParkFilter = true;
            LambdaQueryWrapper<ParkInfo> districtWrapper = new LambdaQueryWrapper<>();
            districtWrapper.eq(ParkInfo::getDistrictName, districtName);
            districtWrapper.select(ParkInfo::getId);
            parkIds.addAll(parkMapper.selectObjs(districtWrapper).stream()
                .map(o -> ((Number) o).longValue()).collect(Collectors.toList()));
        }

        // 园区名称筛选
        if (parkName != null && !parkName.isEmpty()) {
            hasParkFilter = true;
            LambdaQueryWrapper<ParkInfo> nameWrapper = new LambdaQueryWrapper<>();
            nameWrapper.like(ParkInfo::getParkName, parkName);
            nameWrapper.select(ParkInfo::getId);
            List<Long> nameParkIds = parkMapper.selectObjs(nameWrapper).stream()
                .map(o -> ((Number) o).longValue()).collect(Collectors.toList());
            if (parkIds.isEmpty()) {
                parkIds.addAll(nameParkIds);
            } else {
                parkIds.retainAll(nameParkIds);
            }
        }

        // 园区类型筛选
        if (parkType != null && !parkType.isEmpty()) {
            hasParkFilter = true;
            LambdaQueryWrapper<ParkInfo> typeWrapper = new LambdaQueryWrapper<>();
            typeWrapper.eq(ParkInfo::getParkType, Integer.parseInt(parkType));
            typeWrapper.select(ParkInfo::getId);
            List<Long> typeParkIds = parkMapper.selectObjs(typeWrapper).stream()
                .map(o -> ((Number) o).longValue()).collect(Collectors.toList());
            if (parkIds.isEmpty()) {
                parkIds.addAll(typeParkIds);
            } else {
                parkIds.retainAll(typeParkIds);
            }
        }
        
        // 如果有园区筛选条件但没有匹配的园区，直接返回0
        if (hasParkFilter && parkIds.isEmpty()) {
            return 0L;
        }
        
        // 如果有园区ID筛选条件
        if (!parkIds.isEmpty()) {
            wrapper.in(EvaluationRecord::getParkId, parkIds);
        }

        // 参评状态筛选
        if (evaluationStatus != null) {
            wrapper.eq(EvaluationRecord::getEvalStatus, evaluationStatus);
        }

        // 评价年份筛选
        if (evaluationYear != null) {
            wrapper.eq(EvaluationRecord::getEvalYear, evaluationYear);
        }

        return evaluationMapper.selectCount(wrapper);
    }

    /**
     * 统一状态筛选逻辑
     * 支持: "pending"(待审核), "audited"(已审核), 以及具体状态数字 "0"/"1"/"2"/"3"/"4"
     */
    private void applyStatusFilter(LambdaQueryWrapper<EvaluationRecord> wrapper, String status, Integer roleType) {
        // 先判断是否为具体状态数字筛选（前端卡片点击传入）
        if (status != null && !status.isEmpty()) {
            try {
                int statusCode = Integer.parseInt(status);
                wrapper.eq(EvaluationRecord::getStatus, statusCode);
                return;
            } catch (NumberFormatException ignored) {
                // 不是数字，继续按字符串匹配
            }
        }

        // 字符串筛选或默认
        if (roleType == 2) {
            if ("pending".equals(status)) {
                wrapper.eq(EvaluationRecord::getStatus, 1);
            } else if ("audited".equals(status)) {
                wrapper.in(EvaluationRecord::getStatus, Arrays.asList(3, 4));
            } else {
                // 默认显示所有状态（0=未提交, 1=待区县审, 2=区县通过/待市局审, 3=通过, 4=驳回）
                wrapper.in(EvaluationRecord::getStatus, Arrays.asList(0, 1, 2, 3, 4));
            }
        } else if (roleType == 1) {
            if ("pending".equals(status)) {
                wrapper.eq(EvaluationRecord::getStatus, 2);
            } else if ("audited".equals(status)) {
                wrapper.in(EvaluationRecord::getStatus, Arrays.asList(3, 4));
            } else {
                wrapper.in(EvaluationRecord::getStatus, Arrays.asList(2, 3, 4));
            }
        }
    }

    /**
     * 查询待审核列表
     *
     * @param pageNum       页码
     * @param pageSize      每页数量
     * @param roleType      角色类型
     * @param districtName  区县名称（区县管理员时使用）
     * @return 分页结果
     */
    public IPage<EvaluationRecord> getPendingAuditPage(Integer pageNum, Integer pageSize,
                                                        Integer roleType, String districtName) {
        return getAuditPage(pageNum, pageSize, "pending", roleType, districtName);
    }

    /**
     * 查询已审核列表
     *
     * @param pageNum       页码
     * @param pageSize      每页数量
     * @param roleType      角色类型
     * @param districtName  区县名称（区县管理员时使用）
     * @return 分页结果
     */
    public IPage<EvaluationRecord> getAuditedPage(Integer pageNum, Integer pageSize,
                                                    Integer roleType, String districtName) {
        return getAuditPage(pageNum, pageSize, "audited", roleType, districtName);
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
        if (auditDTO.getAction() != 1 && auditDTO.getAction() != 2 && auditDTO.getAction() != 3) {
            throw new BusinessException(ResultCode.PARAM_ERROR, "审核动作必须为 1(通过)、2(驳回) 或 3(直接判D档)");
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
            } else if (auditDTO.getAction() == 3) {
                newStatus = 6; // 6=已终止(直接判D档)
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
            } else if (auditDTO.getAction() == 3) {
                newStatus = 6; // 6=已终止(直接判D档)
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
