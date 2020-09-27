package com.cloud.provider.mapper;


import com.cloud.entity.dbo.OtaRateplanDBO;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface OtaRateplanMapper {
    List<OtaRateplanDBO> getRateplanBySupplierCode(@Param("supplierCode") String supplierCode);

    int insert(OtaRateplanDBO info);

    List<OtaRateplanDBO> getListBySupplierHotelId(@Param("supplierCode") int supplierCode
            , @Param("supplierHotelId") String supplierHotelId
            , @Param("elongHotelId") String elongHotelId);

    int updateStatusById(@Param("ids") List<Integer> ids
            , @Param("enable") int enable
            , @Param("operator") String operator);

    int getOtaRateplanCount(@Param("supplierCode") int supplierCode
            , @Param("supplierHotelId") String supplierHotelId, @Param("groupKey") String groupKey);

    int updateOtaRateplanStatusByProduct(OtaRateplanDBO otaRateplanDBO);

}