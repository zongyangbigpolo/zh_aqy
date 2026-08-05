<template>
  <div class="app-container">
    <el-tabs type="border-card">
      <el-tab-pane :label="item.detail.eqmtName" v-for="item in details">
        <el-descriptions class="margin-top" title="项目信息" :column="3" :size="size" border direction="vertical">
          <el-descriptions-item>
            <template slot="label">
              设备名称
            </template>
            {{ item.detail.eqmtName }}
          </el-descriptions-item>
          <el-descriptions-item>
            <template slot="label">
              设备编码
            </template>
            {{ item.detail.eqmtCode }}
          </el-descriptions-item>
          <el-descriptions-item v-if="item.eqmtTypeSymbol === 'WY'">
            <template slot="label">
              靶标序号
            </template>
            {{ item.detail.sortNum }}
          </el-descriptions-item>
          <el-descriptions-item v-if="item.eqmtTypeSymbol === 'WY'">
            <template slot="label">
              视觉测量仪名称
            </template>
            {{ item.detail.visualEqmtName }}
          </el-descriptions-item>
          <el-descriptions-item v-if="item.eqmtTypeSymbol === 'WY'">
            <template slot="label">
              视觉测量仪编码
            </template>
            {{ item.detail.visualEqmtCode }}
          </el-descriptions-item>
          <el-descriptions-item v-if="item.eqmtTypeSymbol === 'WY'">
            <template slot="label">
              初始位移(mm)
            </template>
            {{ item.detail.initialX }}
          </el-descriptions-item>
          <el-descriptions-item  v-if="item.eqmtTypeSymbol === 'QJ'">
            <template slot="label">
              初始倾角X(°)
            </template>
            {{ item.detail.initialX }}
          </el-descriptions-item>
          <el-descriptions-item  v-if="item.eqmtTypeSymbol === 'QJ'">
            <template slot="label">
              初始倾角Y(°)
            </template>
            {{ item.detail.initialY }}
          </el-descriptions-item>
          <el-descriptions-item  v-if="item.eqmtTypeSymbol === 'QJ'">
            <template slot="label">
              初始倾角Z(°)
            </template>
            {{ item.detail.initialH }}
          </el-descriptions-item>
          <el-descriptions-item v-if="item.eqmtTypeSymbol === 'LF'">
            <template slot="label">
              初始裂缝(mm)
            </template>
            {{ item.detail.initialX }}
          </el-descriptions-item>

          <el-descriptions-item  v-for="(item, index) in item.detail.eqmtFileList" :key="index">
            <template slot="label">
              <i class="el-icon-files"></i>
              {{ item.fileName }}
            </template>
            <el-button type="primary" icon="el-icon-document" @click="handleDownload(item.fileUrl)"></el-button>
          </el-descriptions-item>

        </el-descriptions>
        <div class="content">
          <h3 style="margin-top: 20px; color:#303133; font-weight: bold;">数据采集记录</h3>
          <el-table
            :data="item.rawRecors"
            border
            height="400px"
          >
            <el-table-column label="采集时间" align="center" prop="catchTime" sortable>
              <template slot-scope="scope">
            <span v-if="scope.row.catchTime != null">
              {{ parseTime(scope.row.catchTime, "{y}-{m}-{d} {h}:{i}:{s}") }}
            </span>
              </template>
            </el-table-column>
            <el-table-column label="位移(mm)" align="center" prop="valueWy"
                             v-if="eqmtType && eqmtType.eqmtTypeSymbol === 'WY'"/>
            <el-table-column label="位移累计变化量(mm)" align="center"
                             v-if="eqmtType && eqmtType.eqmtTypeSymbol === 'WY'">
              <template slot-scope="scope">
                {{item.detail != null ? (scope.row.valueWy - item.detail.initialX).toFixed(2) : ''}}
              </template>
            </el-table-column>
            <el-table-column label="裂缝(mm)" align="center" prop="lfValue"
                             v-if="eqmtType && eqmtType.eqmtTypeSymbol === 'LF'"/>
            <el-table-column label="裂缝累计变化量(mm)" align="center"
                             v-if="eqmtType && eqmtType.eqmtTypeSymbol === 'LF'">
              <template slot-scope="scope">
                {{item.detail != null ? (scope.row.lfValue - item.detail.initialX).toFixed(3) : ''}}
              </template>
            </el-table-column>
            <el-table-column label="X角度(°)" align="center" prop="xvalueQj"
                             v-if="eqmtType && eqmtType.eqmtTypeSymbol === 'QJ'"/>
            <el-table-column label="X角度累计变化量(°)" align="center"
                             v-if="eqmtType && eqmtType.eqmtTypeSymbol === 'QJ'">
              <template slot-scope="scope">
                {{item.detail != null ? (scope.row.xvalueQj - item.detail.initialX).toFixed(3) : ''}}
              </template>
            </el-table-column>

            <el-table-column label="Y角度(°)" align="center" prop="yvalueQj"
                             v-if="eqmtType && eqmtType.eqmtTypeSymbol === 'QJ'"/>
            <el-table-column label="Y角度累计变化量(°)" align="center"
                             v-if="eqmtType && eqmtType.eqmtTypeSymbol === 'QJ'">
              <template slot-scope="scope">
                {{item.detail != null ? (scope.row.yvalueQj - item.detail.initialY).toFixed(3) : ''}}
              </template>
            </el-table-column>
            <el-table-column label="Z角度(°)" align="center" prop="zvalueQj"
                             v-if="eqmtType && eqmtType.eqmtTypeSymbol === 'QJ'"/>
            <el-table-column label="Z角度累计变化量(°)" align="center"
                             v-if="eqmtType && eqmtType.eqmtTypeSymbol === 'QJ'">
              <template slot-scope="scope">
                {{item.detail != null ? (scope.row.zvalueQj - item.detail.initialH).toFixed(3) : ''}}
              </template>
            </el-table-column>

            <el-table-column label="水位(mm)" align="center" prop="ylValue"
                             v-if="eqmtType && eqmtType.eqmtTypeSymbol === 'YL'"/>
            <el-table-column label="水位累计变化量(mm)" align="center"
                             v-if="eqmtType && eqmtType.eqmtTypeSymbol === 'YL'">
              <template slot-scope="scope">
                {{item.detail != null ? (scope.row.ylValue - item.detail.initialX).toFixed(1) : ''}}
              </template>
            </el-table-column>
          </el-table>
        </div>
      </el-tab-pane>
    </el-tabs>

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
      size: 2,
      detailsShow: false,
      sectionList: [],
      eqmtTypeOptions: [],
      eqmtFileList: [],
      details: [],
      eqmtType: '',
      rawRecors: [],
      one: false,
      two: false,
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
      this.getEquipmentTypes(data.id);
      this.getEquipmentRecords(data);
    },
    getEquipmentRecords(data) {
      if (!data)
        return false;

      getAqyEquipmentType(data.eqmtTypeId).then(response => {
        this.details = [];
        this.eqmtType = response.data;
        if (this.eqmtType) {
          switch (this.eqmtType.eqmtTypeSymbol) {
            case "WY":
              listAqyEquipment({eqmtTypeId: this.eqmtType.id, sortNum: data.sortNum}).then(response1 => {
                if(response1.rows){
                  for(let it = 0; it < response1.rows.length; it++){
                    listAqyEquipmentWyRaw({eqmtId: response1.rows[it].id}).then(response2 => {
                      this.details.push({
                        eqmtTypeSymbol: this.eqmtType.eqmtTypeSymbol,
                        detail: response1.rows[it],
                        rawRecors: response2.rows
                      })
                    });
                  }
                }
              });

              break;
            case "LF":
              listAqyEquipmentLfRaw({eqmtId: data.id}).then(response => {
                this.details.push({
                  eqmtTypeSymbol: this.eqmtType.eqmtTypeSymbol,
                  detail: data,
                  rawRecors: response.rows
                })
              });
              break;
            case "QJ":
              listAqyEquipmentQjRaw({eqmtId: data.id}).then(response => {
                this.details.push({
                  eqmtTypeSymbol: this.eqmtType.eqmtTypeSymbol,
                  detail: data,
                  rawRecors: response.rows
                })
              });
              break;
            case "YL":
              listAqyEquipmentYlRaw({eqmtId: data.id}).then(response => {
                this.details.push({
                  eqmtTypeSymbol: this.eqmtType.eqmtTypeSymbol,
                  detail: data,
                  rawRecors: response.rows
                })
              });
              break;
          }
        }
      });
    },
    handleDownload(fileUrl) {
      window.open(process.env.VUE_APP_BASE_API + fileUrl, '_blank');
    },
  }
}
</script>

<style scoped>

</style>
