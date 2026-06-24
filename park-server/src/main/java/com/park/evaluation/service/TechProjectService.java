package com.park.evaluation.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.park.evaluation.dto.TechProjectSaveDTO;
import com.park.evaluation.entity.TechProject;
import com.park.evaluation.mapper.TechProjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * 院所合作项目 Service
 *
 * @author park-team
 */
@Slf4j
@Service
public class TechProjectService {

    @Autowired
    private TechProjectMapper techProjectMapper;

    /**
     * 根据评价记录ID查询项目列表
     */
    public List<TechProject> listByEvaluationId(Long evaluationId) {
        return techProjectMapper.selectList(
                new LambdaQueryWrapper<TechProject>()
                        .eq(TechProject::getEvaluationId, evaluationId)
                        .orderByAsc(TechProject::getId)
        );
    }

    /**
     * 保存
     */
    @Transactional(rollbackFor = Exception.class)
    public Long save(TechProjectSaveDTO dto) {
        TechProject entity = dto.toEntity();
        if (entity.getId() == null) {
            techProjectMapper.insert(entity);
        } else {
            techProjectMapper.updateById(entity);
        }
        return entity.getId();
    }

    /**
     * 批量保存
     */
    @Transactional(rollbackFor = Exception.class)
    public void batchSave(Long evaluationId, List<TechProjectSaveDTO> dtos) {
        techProjectMapper.delete(
                new LambdaQueryWrapper<TechProject>()
                        .eq(TechProject::getEvaluationId, evaluationId)
        );
        if (dtos != null) {
            for (TechProjectSaveDTO dto : dtos) {
                dto.setId(null);
                dto.setEvaluationId(evaluationId);
                techProjectMapper.insert(dto.toEntity());
            }
        }
    }

    /**
     * 根据ID查询
     */
    public TechProject getById(Long id) {
        return techProjectMapper.selectById(id);
    }

    /**
     * 删除
     */
    @Transactional(rollbackFor = Exception.class)
    public void deleteById(Long id) {
        techProjectMapper.deleteById(id);
    }
}
