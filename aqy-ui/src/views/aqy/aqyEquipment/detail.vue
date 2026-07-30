<template>
  <div class="app-container">
      <el-descriptions class="margin-top" title="项目信息" :column="3" :size="size" border direction="vertical">
        <el-descriptions-item>
          <template slot="label">
            设备名称
          </template>
          {{ this.detail.eqmtName }}
        </el-descriptions-item>
        <el-descriptions-item>
          <template slot="label">
            设备编码
          </template>
          {{ this.detail.eqmtCode }}
        </el-descriptions-item>
        <el-descriptions-item v-if="detail.eqmtTypeSymbol === 'WY'">
          <template slot="label">
            靶标序号
          </template>
          {{ this.detail.sortNum }}
        </el-descriptions-item>
<!--        <el-descriptions-item>-->
<!--          <template slot="label">-->
<!--            设备类型-->
<!--          </template>-->
<!--          {{ this.detail.eqmtTypeId }}-->
<!--        </el-descriptions-item>-->
<!--        <el-descriptions-item>-->
<!--          <template slot="label">-->
<!--            网关-->
<!--          </template>-->
<!--          {{ this.detail.gatwayId }}-->
<!--        </el-descriptions-item>-->
        <el-descriptions-item v-if="detail.eqmtTypeSymbol === 'WY'">
          <template slot="label">
            视觉测量仪名称
          </template>
          {{ this.detail.visualEqmtName }}
        </el-descriptions-item>
        <el-descriptions-item v-if="detail.eqmtTypeSymbol === 'WY'">
          <template slot="label">
            视觉测量仪编码
          </template>
          {{ this.detail.visualEqmtCode }}
        </el-descriptions-item>
<!--        <el-descriptions-item>-->
<!--          <template slot="label">-->
<!--            经度-->
<!--          </template>-->
<!--          {{ this.detail.longitude }}-->
<!--        </el-descriptions-item>-->
<!--        <el-descriptions-item>-->
<!--          <template slot="label">-->
<!--            纬度-->
<!--          </template>-->
<!--          {{ this.detail.latitude }}-->
<!--        </el-descriptions-item>-->
<!--        <el-descriptions-item>-->
<!--          <template slot="label">-->
<!--            高程-->
<!--          </template>-->
<!--          {{ this.detail.elevation }}-->
<!--        </el-descriptions-item>-->
<!--        <el-descriptions-item>-->
<!--          <template slot="label">-->
<!--            方位角-->
<!--          </template>-->
<!--          {{ this.detail.azimuthAngle }}-->
<!--        </el-descriptions-item>-->
        <el-descriptions-item v-if="detail.eqmtTypeSymbol === 'WY'">
          <template slot="label">
            初始位移(mm)
          </template>
          {{ this.detail.initialX }}
        </el-descriptions-item>
        <el-descriptions-item  v-if="detail.eqmtTypeSymbol === 'QJ'">
          <template slot="label">
            初始倾角X(°)
          </template>
          {{ this.detail.initialX }}
        </el-descriptions-item>
        <el-descriptions-item  v-if="detail.eqmtTypeSymbol === 'QJ'">
          <template slot="label">
            初始倾角Y(°)
          </template>
          {{ this.detail.initialY }}
        </el-descriptions-item>
        <el-descriptions-item  v-if="detail.eqmtTypeSymbol === 'QJ'">
          <template slot="label">
            初始倾角Z(°)
          </template>
          {{ this.detail.initialH }}
        </el-descriptions-item>
        <el-descriptions-item v-if="detail.eqmtTypeSymbol === 'LF'">
          <template slot="label">
            初始裂缝(mm)
          </template>
          {{ this.detail.initialX }}
        </el-descriptions-item>

        <el-descriptions-item  v-for="(item, index) in this.detail.eqmtFileList" :key="index">
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
          :data="rawRecors"
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
              {{detail != null ? (scope.row.valueWy - detail.initialX).toFixed(2) : ''}}
            </template>
          </el-table-column>
          <el-table-column label="位移照片" align="center" prop="picture" v-if="eqmtType && eqmtType.eqmtTypeSymbol === 'WY'">
            <template slot-scope="scope">
              <el-image
                v-if="scope.row.picture"
                style="width: 50px; height: 50px"
                :src="scope.row.picture"
                :preview-src-list="[scope.row.picture]">
              </el-image>
              <span v-else>--</span>
            </template>
          </el-table-column>
          <el-table-column label="裂缝(mm)" align="center" prop="lfValue"
                           v-if="eqmtType && eqmtType.eqmtTypeSymbol === 'LF'"/>
          <el-table-column label="裂缝累计变化量(mm)" align="center"
                           v-if="eqmtType && eqmtType.eqmtTypeSymbol === 'LF'">
            <template slot-scope="scope">
              {{detail != null ? (scope.row.lfValue - detail.initialX).toFixed(3) : ''}}
            </template>
          </el-table-column>
          <el-table-column label="X角度(°)" align="center" prop="xvalueQj"
                           v-if="eqmtType && eqmtType.eqmtTypeSymbol === 'QJ'"/>
          <el-table-column label="X角度累计变化量(°)" align="center"
                           v-if="eqmtType && eqmtType.eqmtTypeSymbol === 'QJ'">
            <template slot-scope="scope">
              {{detail != null ? (scope.row.xvalueQj - detail.initialX).toFixed(3) : ''}}
            </template>
          </el-table-column>

          <el-table-column label="Y角度(°)" align="center" prop="yvalueQj"
                           v-if="eqmtType && eqmtType.eqmtTypeSymbol === 'QJ'"/>
          <el-table-column label="Y角度累计变化量(°)" align="center"
                           v-if="eqmtType && eqmtType.eqmtTypeSymbol === 'QJ'">
            <template slot-scope="scope">
              {{detail != null ? (scope.row.yvalueQj - detail.initialY).toFixed(3) : ''}}
            </template>
          </el-table-column>
          <el-table-column label="Z角度(°)" align="center" prop="zvalueQj"
                           v-if="eqmtType && eqmtType.eqmtTypeSymbol === 'QJ'"/>
          <el-table-column label="Z角度累计变化量(°)" align="center"
                           v-if="eqmtType && eqmtType.eqmtTypeSymbol === 'QJ'">
            <template slot-scope="scope">
              {{detail != null ? (scope.row.zvalueQj - detail.initialH).toFixed(3) : ''}}
            </template>
          </el-table-column>

          <el-table-column label="水位(mm)" align="center" prop="ylValue"
                           v-if="eqmtType && eqmtType.eqmtTypeSymbol === 'YL'"/>
          <el-table-column label="水位累计变化量(mm)" align="center"
                           v-if="eqmtType && eqmtType.eqmtTypeSymbol === 'YL'">
            <template slot-scope="scope">
              {{detail != null ? (scope.row.ylValue - detail.initialX).toFixed(1) : ''}}
            </template>
          </el-table-column>
        </el-table>
      </div>
  </div>
</template>

<script>
import {getAqyEquipmentType, listAqyEquipmentType} from "@/api/aqy/aqyEquipmentType";
import {listSection, listTree} from "@/api/aqy/section";
import {listAqyEquipmentWyRaw} from "@/api/aqy/aqyEquipmentWyRaw";
import {listAqyEquipmentLfRaw} from "@/api/aqy/aqyEquipmentLfRaw";
import {listAqyEquipmentQjRaw} from "@/api/aqy/aqyEquipmentQjRaw";
import {listAqyEquipmentYlRaw} from "@/api/aqy/aqyEquipmentYlRaw";

export default {
  name: "detail",
  dicts: ['project_type'],
  data() {
    return {
      size: '100',
      detailsShow: false,
      sectionList: [],
      eqmtTypeOptions: [],
      eqmtFileList: [],
      detail: {},
      eqmtType: '',
      rawRecors: []
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
      this.detail = data;
      this.getEquipmentTypes(this.detail.id);
      this.getEquipmentRecords();
    },
    getEquipmentRecords() {
      if (!this.detail)
        return false;

      getAqyEquipmentType(this.detail.eqmtTypeId).then(response => {
        this.eqmtType = response.data;
        this.detail.eqmtTypeSymbol = this.eqmtType.eqmtTypeSymbol;
        if (this.eqmtType) {
          switch (this.eqmtType.eqmtTypeSymbol) {
            case "WY":
              listAqyEquipmentWyRaw({eqmtId: this.detail.id}).then(response => {
                this.rawRecors = response.rows;
              });
              break;
            case "LF":
              listAqyEquipmentLfRaw({eqmtId: this.detail.id}).then(response => {
                this.rawRecors = response.rows;
              });
              break;
            case "QJ":
              listAqyEquipmentQjRaw({eqmtId: this.detail.id}).then(response => {
                this.rawRecors = response.rows;
              });
              break;
            case "YL":
              listAqyEquipmentYlRaw({eqmtId: this.detail.id}).then(response => {
                this.rawRecors = response.rows;
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
