<template>
  <div class="app-container">
    <el-descriptions class="margin-top" title="智能网关信息" :column="2" size="medium" border>
      <el-descriptions-item>
        <template slot="label">
          <i class="el-icon-user"></i>
          网关名称
        </template>
        {{gatwayDetails.gatwayName}}
      </el-descriptions-item>
      <el-descriptions-item>
        <template slot="label">
          <i class="el-icon-location-outline"></i>
          网关编号
        </template>
        {{gatwayDetails.gatwayCode}}
      </el-descriptions-item>
    </el-descriptions>
    <el-row>
      <div>管辖的采集设备</div>
      <el-table v-loading="loading" :data="gatwayEquipments">
        <el-table-column label="设备类型" align="center" prop="eqmtTypeId">
          <template slot-scope="scope">
            <span>{{ functionAqyEquipmentType(scope.row.eqmtTypeId) }}</span>
          </template>
        </el-table-column>
        <el-table-column label="设备名称" align="center" prop="eqmtName" />
        <el-table-column label="设备编号" align="center" prop="eqmtCode" />
      </el-table>
    </el-row>
  </div>
</template>

<script>
import {getAqyEquipmentType, listAqyEquipmentType} from "@/api/aqy/aqyEquipmentType";
import {listSection, listTree} from "@/api/aqy/section";
import {listAqyEquipmentWyRaw} from "@/api/aqy/aqyEquipmentWyRaw";
import {listAqyEquipmentLfRaw} from "@/api/aqy/aqyEquipmentLfRaw";
import {listAqyEquipmentQjRaw} from "@/api/aqy/aqyEquipmentQjRaw";
import {listAqyEquipmentYlRaw} from "@/api/aqy/aqyEquipmentYlRaw";
import {listAqyEquipment} from "@/api/aqy/aqyEquipment";

export default {
  name: "detail",
  dicts: ['project_type'],
  data() {
    return {
      // 遮罩层
      loading: false,
      gatwayDetails: {},
      gatwayEquipments: [],
      eqmtTypeOptions: [],
    }
  },
  methods: {
    init(data) {
      this.gatwayDetails = data;
      listAqyEquipment({gatwayId: data.id}).then(response => {
        this.gatwayEquipments = response.rows;
      });
      this.getEquipmentTypes(this.gatwayDetails.projectId);
    },
    getEquipmentTypes(projectId) {
      listAqyEquipmentType({projectId: projectId}).then(response => {
        this.eqmtTypeOptions = response.rows;
      });
    },
    functionAqyEquipmentType(eqmtTypeId) {
      const eqmtType = this.eqmtTypeOptions.find(item => item.id === eqmtTypeId)
      return eqmtType ? eqmtType.eqmtTypeName : ''
    },
  }
}
</script>

<style scoped>

</style>
