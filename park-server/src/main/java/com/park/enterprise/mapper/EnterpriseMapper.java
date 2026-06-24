package com.park.enterprise.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.park.enterprise.entity.EnterpriseInfo;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

/**
 * 入驻企业 Mapper
 *
 * @author park-team
 */
@Mapper
public interface EnterpriseMapper extends BaseMapper<EnterpriseInfo> {

    IPage<EnterpriseInfo> selectEnterprisePageWithJoin(Page<EnterpriseInfo> page,
                                                        @Param("keyword") String keyword,
                                                        @Param("enterpriseName") String enterpriseName,
                                                        @Param("parkId") Long parkId,
                                                        @Param("parkIds") java.util.List<Long> parkIds,
                                                        @Param("districtId") Long districtId,
                                                        @Param("industryName") String industryName,
                                                        @Param("honor") String honor,
                                                        @Param("status") String status,
                                                        @Param("registerStatus") String registerStatus,
                                                        @Param("isParticipate") Integer isParticipate);

    EnterpriseInfo selectEnterpriseByIdWithJoin(@Param("id") Long id);
}
