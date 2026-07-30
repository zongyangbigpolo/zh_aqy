<template>
  <div class="app-container">
    <el-dialog :title="titleProfile" :visible.sync="open" width="80%" :close-on-click-modal="false" class="showAll_dialog" append-to-body v-el-drag-dialog>
      <el-descriptions class="margin-top" title="项目信息" :column="4" :size="size" border direction="vertical">
        <el-descriptions-item>
          <template slot="label">
            项目名称
          </template>
          {{ this.detail.name }}
        </el-descriptions-item>
        <el-descriptions-item>
          <template slot="label">
            项目类型
          </template>
          <dict-tag :options="dict.type.project_type" :value="this.detail.projectType"/>
        </el-descriptions-item>
        <el-descriptions-item>
          <template slot="label">
            所属企业
          </template>
          {{ this.detail.companyName }}
        </el-descriptions-item>
        <el-descriptions-item>
          <template slot="label">
            经度
          </template>
          {{ this.detail.longitude }}
        </el-descriptions-item>
        <el-descriptions-item>
          <template slot="label">
            纬度
          </template>
          {{ this.detail.latitude }}
        </el-descriptions-item>
        <el-descriptions-item>
          <template slot="label">
            高程
          </template>
          {{ this.detail.elevation }}
        </el-descriptions-item>
        <el-descriptions-item>
          <template slot="label">
            航向角
          </template>
          {{ this.detail.courseAngle }}
        </el-descriptions-item>
        <el-descriptions-item>
          <template slot="label">
            俯视角
          </template>
          {{ this.detail.depressionAngle }}
        </el-descriptions-item>
      </el-descriptions>
      <div class="content">
        <h3 style="margin-top: 20px; color:#303133; font-weight: bold;">监测断面</h3>
        <el-table
          v-if="detailsShow"
          :data="sectionList"
          row-key="rowKey"
          :default-expand-all="true"
          border
          :tree-props="{children: 'children', hasChildren: 'hasChildren'}"
        >
          <el-table-column label="断面名称" align="center" prop="sectionName"/>
          <el-table-column label="设备类型" align="center" prop="eqmtTypeId">
            <template slot-scope="scope">
              <span>{{ functionAqyEquipmentType(scope.row.eqmtTypeId) }}</span>
            </template>
          </el-table-column>
          <el-table-column label="采集设备" align="center" prop="eqmtName"/>
        </el-table>
      </div>
    </el-dialog>
  </div>
</template>

<script>
import {listAqyEquipmentType} from "@/api/aqy/aqyEquipmentType";
import {listSection,listTree} from "@/api/aqy/section";

export default {
  name: "detail",
  dicts: ['project_type'],
  data() {
    return {
      size: '100',
      titleProfile: null,
      detailsShow: false,
      sectionList: [],
      eqmtTypeOptions: [],
      open: false,
      detail: {}
    }
  },
  computed: {},
  created() {

  },
  methods: {
    /** 查询设备类型列表 */
    getEquipmentTypes(projectId) {
      listAqyEquipmentType({projectId: projectId}).then(response => {
        this.eqmtTypeOptions = response.rows;
      });
    },
    functionAqyEquipmentType(eqmtTypeId) {
      const eqmtType = this.eqmtTypeOptions.find(item => item.id === eqmtTypeId)
      return eqmtType ? eqmtType.eqmtTypeName : ''
    },
    init(data) {
      this.detailsShow = false;
      this.titleProfile = "【" + data.name + "】详情";
      this.detail = data;
      this.getEquipmentTypes(this.detail.id);
      this.getListSection();
    },


    /** 查询断面信息列表 */
    getListSection() {
      listTree(this.detail.id).then(response => {
        this.sectionList = response.rows;

        this.open = true;
        this.detailsShow = true;
      });
    },
  }

}
</script>

<style scoped>

</style>
