<template>
  <div class="app-container">
    <el-dialog :title="titleProfile" :visible.sync="open" width="80%" :close-on-click-modal="false" class="showAll_dialog" append-to-body v-el-drag-dialog>
      <el-descriptions class="margin-top" title="项目信息" :column="2" :size="size" border direction="vertical">
        <el-descriptions-item>
          <template slot="label">
            项目名称
          </template>
          {{ this.detail.projectName }}
        </el-descriptions-item>
        <el-descriptions-item>
          <template slot="label">
            断面名称
          </template>
          {{ this.detail.sectionName }}
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
      </el-descriptions>
      <div class="content">
        <h3 style="margin-top: 20px; color:#303133; font-weight: bold;">监测断面</h3>
        <el-table
          v-if="detailsShow"
          :data="details"
          row-key="id"
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


import {listProject} from "@/api/aqy/project";
import {listAqyEquipmentType} from "@/api/aqy/aqyEquipmentType";

export default {
  name: "detail",
  dicts: ['project_type'],
  data() {
    return {
      size: '100',
      titleProfile: null,
      detailsShow: false,
      projectOptions: [],
      eqmtTypeOptions: [],
      open: false,
      detail: {},
      details: []
    }
  },
  computed: {},
  created() {

  },
  methods: {
    getProjects() {
      listProject().then(response => {
        this.projectOptions = response.rows;
        const project = this.projectOptions.find(item => item.id === this.detail.projectId)
        if (project) {
          this.detail.projectName = project.name
        }
        this.getEquipmentTypes();
        this.open = true;
        this.detailsShow = true;
      });
    },
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
      this.titleProfile = "【" + data.sectionName + "】详情";
      this.detail = data;
      this.details = [data];
      this.getProjects();
    },
    reset() {
      this.detail = {};
      this.details = [];
    },
  }

}
</script>

<style scoped>

</style>
