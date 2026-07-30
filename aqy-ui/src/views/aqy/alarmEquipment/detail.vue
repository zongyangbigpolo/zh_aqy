<template>
  <div class="app-container">
    <el-descriptions class="margin-top" title="声光报警设备信息" :column="2" size="medium" border>
      <el-descriptions-item>
        <template slot="label">
          <i class="el-icon-user"></i>
          设备名称
        </template>
        {{gatwayDetails.eqmtName}}
      </el-descriptions-item>
      <el-descriptions-item>
        <template slot="label">
          <i class="el-icon-location-outline"></i>
          设备编号
        </template>
        {{gatwayDetails.eqmtCode}}
      </el-descriptions-item>
    </el-descriptions>
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
  data() {
    return {
      // 遮罩层
      loading: false,
      gatwayDetails: {},
    }
  },
  methods: {
    init(data) {
      this.gatwayDetails = data;
    }
  }
}
</script>

<style scoped>

</style>
