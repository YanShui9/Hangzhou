package com.park.evaluation.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.park.evaluation.dto.TechInnovationSaveDTO;
import com.park.evaluation.entity.TechInnovation;
import com.park.evaluation.mapper.TechInnovationMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * 科技创新 Service
 *
 * @author park-team
 */
@Slf4j
@Service
public class TechInnovationService {

    @Autowired
    private TechInnovationMapper techInnovationMapper;

    /**
     * 根据评价记录ID查询科技创新列表
     *
     * @param evaluationId 评价记录ID
     * @return 科技创新列表
     */
    public List<TechInnovation> listByEvaluationId(Long evaluationId) {
        return techInnovationMapper.selectList(
                new LambdaQueryWrapper<TechInnovation>()
                        .eq(TechInnovation::getEvaluationId, evaluationId)
                        .orderByAsc(TechInnovation::getId)
        );
    }

    /**
     * 保存科技创新（新增或修改）
     *
     * @param dto 保存请求
     * @return 主键ID
     */
    @Transactional(rollbackFor = Exception.class)
    public Long save(TechInnovationSaveDTO dto) {
        TechInnovation entity = dto.toEntity();
        if (entity.getId() == null) {
            techInnovationMapper.insert(entity);
        } else {
            techInnovationMapper.updateById(entity);
        }
        return entity.getId();
    }

    /**
     * 批量保存科技创新
     *
     * @param evaluationId 评价记录ID
     * @param dtos         科技创新列表
     */
    @Transactional(rollbackFor = Exception.class)
    public void batchSave(Long evaluationId, List<TechInnovationSaveDTO> dtos) {
        // 先删除旧的
        techInnovationMapper.delete(
                new LambdaQueryWrapper<TechInnovation>()
                        .eq(TechInnovation::getEvaluationId, evaluationId)
        );
        // 重新插入
        if (dtos != null) {
            for (TechInnovationSaveDTO dto : dtos) {
                dto.setId(null);
                dto.setEvaluationId(evaluationId);
                techInnovationMapper.insert(dto.toEntity());
            }
        }
    }

    /**
     * 根据ID查询
     *
     * @param id 主键
     * @return 科技创新信息
     */
    public TechInnovation getById(Long id) {
        return techInnovationMapper.selectById(id);
    }

    /**
     * 根据ID删除
     *
     * @param id 主键
     */
    @Transactional(rollbackFor = Exception.class)
    public void deleteById(Long id) {
        techInnovationMapper.deleteById(id);
    }
}
