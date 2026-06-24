package com.park.evaluation.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.park.evaluation.dto.CultivationRecordSaveDTO;
import com.park.evaluation.entity.CultivationRecord;
import com.park.evaluation.mapper.CultivationRecordMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * 企业培育 Service
 *
 * @author park-team
 */
@Slf4j
@Service
public class CultivationRecordService {

    @Autowired
    private CultivationRecordMapper cultivationRecordMapper;

    /**
     * 根据ID查询
     */
    public CultivationRecord getById(Long id) {
        return cultivationRecordMapper.selectById(id);
    }

    /**
     * 根据评价记录ID查询列表
     */
    public List<CultivationRecord> listByEvaluationId(Long evaluationId) {
        return cultivationRecordMapper.selectList(
                new LambdaQueryWrapper<CultivationRecord>()
                        .eq(CultivationRecord::getEvaluationId, evaluationId)
                        .orderByAsc(CultivationRecord::getId)
        );
    }

    /**
     * 保存
     */
    @Transactional(rollbackFor = Exception.class)
    public Long save(CultivationRecordSaveDTO dto) {
        CultivationRecord entity = dto.toEntity();
        if (entity.getId() == null) {
            cultivationRecordMapper.insert(entity);
        } else {
            cultivationRecordMapper.updateById(entity);
        }
        return entity.getId();
    }

    /**
     * 批量保存
     */
    @Transactional(rollbackFor = Exception.class)
    public void batchSave(Long evaluationId, List<CultivationRecordSaveDTO> dtos) {
        cultivationRecordMapper.delete(
                new LambdaQueryWrapper<CultivationRecord>()
                        .eq(CultivationRecord::getEvaluationId, evaluationId)
        );
        if (dtos != null) {
            for (CultivationRecordSaveDTO dto : dtos) {
                dto.setId(null);
                dto.setEvaluationId(evaluationId);
                cultivationRecordMapper.insert(dto.toEntity());
            }
        }
    }

    /**
     * 删除
     */
    @Transactional(rollbackFor = Exception.class)
    public void deleteById(Long id) {
        cultivationRecordMapper.deleteById(id);
    }
}
