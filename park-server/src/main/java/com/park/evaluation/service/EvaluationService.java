package com.park.evaluation.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.park.common.exception.BusinessException;
import com.park.common.result.ResultCode;
import com.park.evaluation.dto.EvaluationQueryDTO;
import com.park.evaluation.dto.EvaluationSaveDTO;
import com.park.evaluation.entity.EvaluationRecord;
import com.park.evaluation.mapper.EvaluationMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * 评价服务
 * 状态定义：0=草稿, 1=待区县审, 2=待市局审, 3=通过, 4=驳回
 *
 * @author park-team
 */
@Slf4j
@Service
public class EvaluationService {

    @Autowired
    private EvaluationMapper evaluationMapper;

    /**
     * 分页查询评价记录
     *
     * @param queryDTO 查询条件
     * @return 分页结果
     */
    public IPage<EvaluationRecord> getEvaluationPage(EvaluationQueryDTO queryDTO) {
        Page<EvaluationRecord> page = new Page<>(queryDTO.getPageNum(), queryDTO.getPageSize());
        LambdaQueryWrapper<EvaluationRecord> wrapper = new LambdaQueryWrapper<>();

        // 构建查询条件
        if (queryDTO.getParkId() != null) {
            wrapper.eq(EvaluationRecord::getParkId, queryDTO.getParkId());
        }
        // 支持多园区查询（区县管理员使用）
        if (queryDTO.getParkIds() != null && !queryDTO.getParkIds().isEmpty()) {
            wrapper.in(EvaluationRecord::getParkId, queryDTO.getParkIds());
        }
        if (queryDTO.getYear() != null) {
            wrapper.eq(EvaluationRecord::getYear, queryDTO.getYear());
        }
        if (queryDTO.getStatus() != null) {
            wrapper.eq(EvaluationRecord::getStatus, queryDTO.getStatus());
        }

        // 默认按创建时间降序排序
        wrapper.orderByDesc(EvaluationRecord::getCreateTime);

        return evaluationMapper.selectPage(page, wrapper);
    }

    /**
     * 根据ID查询评价记录详情
     *
     * @param id 评价记录ID
     * @return 评价记录实体
     */
    public EvaluationRecord getEvaluationById(Long id) {
        EvaluationRecord record = evaluationMapper.selectById(id);
        if (record == null) {
            throw new BusinessException(ResultCode.DATA_NOT_FOUND, "评价记录不存在");
        }
        return record;
    }

    /**
     * 保存评价记录（新增或修改）
     *
     * @param saveDTO 保存请求参数
     */
    @Transactional(rollbackFor = Exception.class)
    public void saveEvaluation(EvaluationSaveDTO saveDTO) {
        // 检查同一园区、年份是否已存在评价记录（排除自身）
        LambdaQueryWrapper<EvaluationRecord> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(EvaluationRecord::getParkId, saveDTO.getParkId())
                .eq(EvaluationRecord::getYear, saveDTO.getYear());
        if (saveDTO.getId() != null) {
            wrapper.ne(EvaluationRecord::getId, saveDTO.getId());
        }
        Long count = evaluationMapper.selectCount(wrapper);
        if (count > 0) {
            throw new BusinessException(ResultCode.DATA_EXISTS, "该园区该年度的评价记录已存在");
        }

        EvaluationRecord record = new EvaluationRecord();
        BeanUtils.copyProperties(saveDTO, record);

        if (saveDTO.getId() != null) {
            // 修改
            EvaluationRecord existing = evaluationMapper.selectById(saveDTO.getId());
            if (existing == null) {
                throw new BusinessException(ResultCode.DATA_NOT_FOUND, "评价记录不存在");
            }
            // 只有草稿状态才能修改
            if (existing.getStatus() != 0) {
                throw new BusinessException(ResultCode.FAILURE, "只有草稿状态的评价记录才能修改");
            }
            evaluationMapper.updateById(record);
            log.info("评价记录修改成功：id={}", saveDTO.getId());
        } else {
            // 新增
            record.setStatus(0); // 0=草稿
            evaluationMapper.insert(record);
            log.info("评价记录新增成功：parkId={}, year={}",
                    saveDTO.getParkId(), saveDTO.getYear());
        }
    }

    /**
     * 提交评价（状态从 0=草稿 改为 1=待区县审）
     *
     * @param id 评价记录ID
     */
    @Transactional(rollbackFor = Exception.class)
    public void submitEvaluation(Long id) {
        EvaluationRecord record = evaluationMapper.selectById(id);
        if (record == null) {
            throw new BusinessException(ResultCode.DATA_NOT_FOUND, "评价记录不存在");
        }
        if (record.getStatus() != 0) {
            throw new BusinessException(ResultCode.FAILURE, "只有草稿状态的评价记录才能提交");
        }

        record.setStatus(1); // 1=待区县审
        evaluationMapper.updateById(record);
        log.info("评价记录已提交：id={}", id);
    }

    /**
     * 区县审核通过（状态从 1=待区县审 改为 2=待市局审）
     *
     * @param id 评价记录ID
     */
    @Transactional(rollbackFor = Exception.class)
    public void districtPass(Long id) {
        updateStatus(id, 1, 2, "区县审核通过");
    }

    /**
     * 区县审核驳回（状态从 1=待区县审 改为 4=驳回）
     *
     * @param id 评价记录ID
     */
    @Transactional(rollbackFor = Exception.class)
    public void districtReject(Long id) {
        updateStatus(id, 1, 4, "区县审核驳回");
    }

    /**
     * 市级审核通过（状态从 2=待市局审 改为 3=通过）
     *
     * @param id 评价记录ID
     */
    @Transactional(rollbackFor = Exception.class)
    public void cityPass(Long id) {
        updateStatus(id, 2, 3, "市级审核通过");
    }

    /**
     * 市级审核驳回（状态从 2=待市局审 改为 4=驳回）
     *
     * @param id 评价记录ID
     */
    @Transactional(rollbackFor = Exception.class)
    public void cityReject(Long id) {
        updateStatus(id, 2, 4, "市级审核驳回");
    }

    /**
     * 检查园区当年是否已提交评价
     *
     * @param parkId 园区ID
     * @param year   评价年份
     * @return true=已提交，false=未提交
     */
    public boolean hasSubmittedEvaluation(Long parkId, Integer year) {
        LambdaQueryWrapper<EvaluationRecord> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(EvaluationRecord::getParkId, parkId)
                .eq(EvaluationRecord::getYear, year)
                .gt(EvaluationRecord::getStatus, 0); // 状态>0表示已提交（待审核、通过、驳回等）
        Long count = evaluationMapper.selectCount(wrapper);
        return count > 0;
    }

    /**
     * 更新评价记录状态
     *
     * @param id             评价记录ID
     * @param expectedStatus 期望的当前状态
     * @param newStatus      新状态
     * @param actionDesc     操作描述（用于日志）
     */
    private void updateStatus(Long id, Integer expectedStatus, Integer newStatus, String actionDesc) {
        EvaluationRecord record = evaluationMapper.selectById(id);
        if (record == null) {
            throw new BusinessException(ResultCode.DATA_NOT_FOUND, "评价记录不存在");
        }
        if (!expectedStatus.equals(record.getStatus())) {
            throw new BusinessException(ResultCode.FAILURE,
                    "当前状态不允许执行此操作，期望状态：" + expectedStatus + "，实际状态：" + record.getStatus());
        }

        record.setStatus(newStatus);
        evaluationMapper.updateById(record);
        log.info("评价记录{}：id={}", actionDesc, id);
    }
}
