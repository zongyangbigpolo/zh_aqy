<template>
  <div class="app-container">
    <el-form :model="queryParams" ref="queryForm" size="small" :inline="true" v-show="showSearch" label-width="68px">
      <el-form-item label="工程项目" prop="projectId">
        <el-select v-model="queryParams.projectId" placeholder="请选择工程项目">
          <el-option v-for="(item, index) in projectOptions" :key='index' :value="item.id" :label="item.name"></el-option>
        </el-select>
      </el-form-item>
      <el-form-item label="设备类型" prop="eqmtTypeId">
        <el-select v-model="queryParams.eqmtTypeId" placeholder="请选择设备类型" @change="handleEmqtTypeChange">
          <el-option v-for="(item, index) in eqmtTypeOptions" :key='index' :value="item.id" :label="item.eqmtTypeName"></el-option>
        </el-select>
      </el-form-item>
      <el-form-item label="设备名称" prop="eqmtId">
        <el-select
          v-model="queryParams.eqmtId"
          placeholder="请选择设备名称"
          :disabled="!queryParams.eqmtTypeId"
          @change="handleEqmtChange"
        >
          <el-option
            v-for="(item, index) in eqmtOptions"
            :key='index'
            :value="item.id"
            :label="item.eqmtName"
          ></el-option>
        </el-select>
      </el-form-item>
      <el-form-item label="采集时间" prop="timeFrame">
        <el-date-picker
          v-model="queryParams.timeFrame"
          type="datetimerange"
          value-format="yyyy-MM-dd HH:mm:ss"
          range-separator="至"
          start-placeholder="开始日期"
          end-placeholder="结束日期">
        </el-date-picker>
      </el-form-item>

      <el-form-item>
        <el-button type="primary" icon="el-icon-search" size="mini" @click="handleQuery">搜索</el-button>
        <el-button icon="el-icon-refresh" size="mini" @click="resetQuery">重置</el-button>
      </el-form-item>
    </el-form>

    <el-row :gutter="10" class="mb8">
      <el-col :span="1.5">
        <el-button
          type="warning"
          plain
          icon="el-icon-download"
          size="mini"
          @click="handleExport"
          v-hasPermi="['aqy:aqyEquipment:export']"
        >导出
        </el-button>
      </el-col>
      <right-toolbar :showSearch.sync="showSearch" @queryTable="getList"></right-toolbar>
    </el-row>

    <el-tabs v-model="activeName" type="card" @tab-click="handleClick">
      <el-tab-pane label="数据表格" name="first">
        <el-table v-loading="loading" :data="dataList" :max-height="tableHeight" border style="width: 100%;">
          <el-table-column label="序号" type="index" align="center"/>
          <el-table-column label="设备类型" align="center" prop="eqmtTypeName"/>
          <el-table-column label="设备名称" align="center" prop="eqmtName"/>
          <el-table-column label="设备编码" align="center" prop="eqmtCode"/>
          <el-table-column label="位移(mm)" align="center" prop="valueWy" v-if="searchType === 'WY'"/>
          <el-table-column label="位移累计变化量(mm)" align="center"
                           v-if="searchType === 'WY'">
            <template slot-scope="scope">
              {{scope.row.initialX != null ? (scope.row.valueWy - scope.row.initialX ).toFixed(2) : ''}}
            </template>
          </el-table-column>

          <el-table-column label="裂缝累计变化量(mm)" align="center"
                           v-if="searchType === 'LF'">
            <template slot-scope="scope">
              {{scope.row.initialX != null ? (scope.row.lfValue - scope.row.initialX).toFixed(3) : ''}}
            </template>
          </el-table-column>
          <el-table-column label="裂缝(mm)" align="center" prop="lfValue" v-if="searchType === 'LF'"/>

          <el-table-column label="X角度(°)" align="center" prop="xvalueQj" v-if="searchType === 'QJ'"/>
          <el-table-column label="X角度累计变化量(°)" align="center"
                           v-if="searchType === 'QJ'">
            <template slot-scope="scope">
              {{scope.row.initialX != null ? (scope.row.xvalueQj - scope.row.initialX).toFixed(3) : ''}}
            </template>
          </el-table-column>
          <el-table-column label="Y角度(°)" align="center" prop="yvalueQj" v-if="searchType === 'QJ'"/>
          <el-table-column label="Y角度累计变化量(°)" align="center"
                           v-if="searchType === 'QJ'">
            <template slot-scope="scope">
              {{scope.row.initialY != null ? (scope.row.yvalueQj - scope.row.initialY).toFixed(3) : ''}}
            </template>
          </el-table-column>

          <el-table-column label="Z角度(°)" align="center" prop="zvalueQj" v-if="searchType === 'QJ'"/>
          <el-table-column label="Z角度累计变化量(°)" align="center"
                           v-if="searchType === 'QJ'">
            <template slot-scope="scope">
              {{scope.row.initialH != null ? (scope.row.zvalueQj - scope.row.initialH).toFixed(3) : ''}}
            </template>
          </el-table-column>

          <el-table-column label="水位(mm)" align="center" prop="ylValue" v-if="searchType === 'YL'"/>
          <el-table-column label="水位累计变化量(mm)" align="center"
                           v-if="searchType === 'YL'">
            <template slot-scope="scope">
              {{scope.row.initialX != null ? (scope.row.ylValue - scope.row.initialX).toFixed(1) : ''}}
            </template>
          </el-table-column>
          <el-table-column label="采集时间" align="center" prop="catchTime">
            <template slot-scope="scope">
              <span v-if="scope.row.catchTime != null">
                {{ parseTime(scope.row.catchTime, "{y}-{m}-{d} {h}:{i}:{s}") }}
              </span>
            </template>
          </el-table-column>
        </el-table>
        <pagination
          v-show="total > 0"
          :total="total"
          :page.sync="queryParams.pageNum"
          :limit.sync="queryParams.pageSize"
          @pagination="getList"
        />
      </el-tab-pane>
      <el-tab-pane label="统计图表" name="second">
        <div ref="chart" class="chart-container"></div>
      </el-tab-pane>
    </el-tabs>
  </div>
</template>
<script>
import { listProject } from "@/api/aqy/project";
import { listAqyEquipmentType } from "@/api/aqy/aqyEquipmentType";
import { listAqyEquipment } from "@/api/aqy/aqyEquipment";
import { listRealTime } from "@/api/aqy/aqyRawReport";
import { parseTime } from "../../../utils/ruoyi";
import * as XLSX from 'xlsx';
import * as echarts from 'echarts';
import moment from 'moment';
import {
  TitleComponent,
  ToolboxComponent,
  TooltipComponent,
  GridComponent,
  DataZoomComponent
} from 'echarts/components';
import { LineChart } from 'echarts/charts';
import { UniversalTransition } from 'echarts/features';
import { CanvasRenderer } from 'echarts/renderers';

echarts.use([
  TitleComponent,
  ToolboxComponent,
  TooltipComponent,
  GridComponent,
  DataZoomComponent,
  LineChart,
  CanvasRenderer,
  UniversalTransition
]);

export default {
  name: "AqyEquipment",
  data() {
    return {
      timeFrame: [],
      myChart: null,
      loading: true,
      total: 0,
      tableHeight: document.documentElement.scrollHeight - 330,
      showSearch: true,
      activeName: 'first',
      queryParams: {
        pageNum: 1,
        pageSize: 50,
        projectId: null,
        eqmtTypeId: null,
        eqmtId: null,
        eqmtCode: null,
        timeFrame: [moment().subtract(1, "months").format('YYYY-MM-DD HH:mm:ss'), moment().format('YYYY-MM-DD HH:mm:ss')]
      },
      projectOptions: [],
      eqmtTypeOptions: [],
      eqmtOptions: [],
      dataList: [],
      searchType: null,
      chartType: 'line',
    };
  },
  created() {
    this.getProjects();
  },
  methods: {
    parseTime,
    getProjects() {
      listProject().then(response => {
        this.projectOptions = response.rows;
        if (response.rows)
          this.queryParams.projectId = response.rows[0].id;
        else
          this.queryParams.projectId = null;

        this.getEquipmentTypes(this.queryParams.projectId);
      });
    },
    getEquipmentTypes(projectId) {
      listAqyEquipmentType({ projectId: projectId }).then(response => {
        this.eqmtTypeOptions = response.rows;
        if (response.rows)
          this.queryParams.eqmtTypeId = response.rows[0].id;
        else
          this.queryParams.eqmtTypeId = null;

        this.handleEmqtTypeChange(this.queryParams.eqmtTypeId);
      });
    },
    getList() {
      this.loading = true;
      let eqmtTypeItem = this.eqmtTypeOptions.filter(item => {
        return item.id === this.queryParams.eqmtTypeId;
      });
      if (eqmtTypeItem && eqmtTypeItem.length > 0) {
        this.searchType = eqmtTypeItem[0].eqmtTypeSymbol;
      }
      listRealTime(this.queryParams).then(response => {
        this.dataList = response.rows;
        this.total = response.total;
        this.loading = false;
        this.updateChartData();
      });
    },
    // 添加设备选择变化的处理方法
    handleEqmtChange(val) {
      this.getList();
    },
    handleEmqtTypeChange(val) {
      // 清空设备名称的选择
      this.queryParams.eqmtId = null;
      this.eqmtOptions = [];

      if (val) {
        // 更新searchType
        let eqmtTypeItem = this.eqmtTypeOptions.find(item => item.id === val);
        if (eqmtTypeItem) {
          this.searchType = eqmtTypeItem.eqmtTypeSymbol;
        }
        // 获取设备列表
        listAqyEquipment({ eqmtTypeId: val }).then(response => {
          this.eqmtOptions = response.rows;
          if(this.eqmtOptions){
            this.queryParams.eqmtId = this.eqmtOptions[0].id;
          }else{
            this.queryParams.eqmtId = null;
          }
        });

        // 清空图表实例
        if (this.myChart) {
          this.myChart.dispose();
          this.myChart = null;
        }
        this.getList()
      }
    },
    updateChartData() {
      console.log(313211232)
      if (this.activeName === 'second') {
        this.renderChart();
      }
    },
    handleClick(tab, event) {
      if (tab.name === 'second') {
        this.$nextTick(() => {
          if (this.myChart) {
            this.myChart.resize();
          }
          this.renderChart();
        });
      }
    },
    renderChart() {
      const chartDom = this.$refs.chart;

      // 如果已存在图表实例，先销毁
      if (this.myChart) {
        this.myChart.dispose();
      }

      this.myChart = echarts.init(chartDom);

      let option = {
        title: {
          text: '统计图表',
          left: 'center',
          textStyle: {
            color: '#fff',
            fontSize: 18,
          }
        },
        tooltip: {
          trigger: 'axis',
          axisPointer: {
            type: 'cross',
            label: {
              backgroundColor: '#6a7985'
            }
          }
        },
        toolbox: {
          feature: {
            saveAsImage: {},
            myTool: {
              show: true,
              title: '切换图表类型',
              icon: 'path://M512 64c-247.4 0-448 200.6-448 448s200.6 448 448 448 448-200.6 448-448-448-448-448-448zm0 832c-211.6 0-384-172.4-384-384s172.4-384 384-384 384 172.4 384 384-172.4 384-384 384zm-128-384h256v64H384v-64zm0 128h256v64H384v-64zm0 128h256v64H384v-64z',
              onclick: () => {
                this.toggleChartType();
              }
            }
          },
          right: '10%',
          top: '5%',
          iconStyle: {
            borderColor: '#fff',
            borderWidth: 1,
            color: '#fff'
          }
        },
        legend: {
          data: this.getSeriesData().map(series => series.name),
          textStyle: {
            color: '#fff'
          },
          top: '5%'
        },
        grid: {
          left: '3%',
          right: '4%',
          bottom: '3%',
          containLabel: true
        },
        xAxis: {
          type: 'category',
          data: this.getXAxisData(),
          axisLabel: {
            textStyle: {
              color: '#fff'
            }
          },
          axisLine: {
            lineStyle: {
              color: '#fff'
            }
          }
        },
        yAxis: {
          type: 'value',
          axisLabel: {
            textStyle: {
              color: '#fff'
            }
          },
          axisLine: {
            lineStyle: {
              color: '#fff'
            }
          },
          splitLine: {
            lineStyle: {
              color: '#57617B'
            }
          }
        },
        series: this.getSeriesData(),
        dataZoom: [
          {
            type: 'slider',
            show: true,
            xAxisIndex: [0],
            start: 0,
            end: 100,
            handleStyle: {
              color: '#1f78b4'
            },
            textStyle: {
              color: '#fff'
            }
          },
          {
            type: 'inside',
            xAxisIndex: [0],
            start: 0,
            end: 100
          }
        ]
      };

      option.series.forEach(series => {
        if (series.type === 'line') {
          series.areaStyle = {
            color: 'rgba(255, 255, 255, 0.3)'
          };
        }
        if (series.type === 'bar') {
          series.barWidth = '50%';
        }
      });

      this.myChart.setOption(option);
    },
    getXAxisData() {
      return this.dataList.map(item => {
        const date = new Date(item.catchTime);
        return `${date.getFullYear()}-${(date.getMonth() + 1).toString().padStart(2, '0')}-${date.getDate().toString().padStart(2, '0')} ${date.getHours().toString().padStart(2, '0')}:${date.getMinutes().toString().padStart(2, '0')}:${date.getSeconds().toString().padStart(2, '0')}`;
      });
    },
    getSeriesData() {
      let series = [];
      switch(this.searchType) {
        case 'WY':
          series = [
            {
              name: '位移累计变化量(mm)',
              type: this.chartType,
              data: this.dataList.map(item => item.valueWy - item.initialX)
            }
          ];
          break;
        case 'LF':
          series = [
            {
              name: '裂缝累计变化量(mm)',
              type: this.chartType,
              data: this.dataList.map(item => item.lfValue - item.initialX)
            }
          ];
          break;
        case 'YL':
          series = [
            {
              name: '水位累计变化量(mm)',
              type: this.chartType,
              data: this.dataList.map(item => item.ylValue - item.initialX)
            }
          ];
          break;
        case 'QJ':
          series = [
            {
              name: 'X角度累计变化量(°)',
              type: this.chartType,
              data: this.dataList.map(item => item.xvalueQj - item.initialX)
            },
            {
              name: 'Y角度累计变化量(°)',
              type: this.chartType,
              data: this.dataList.map(item => item.yvalueQj - item.initialY)
            },
            {
              name: 'Z角度累计变化量(°)',
              type: this.chartType,
              data: this.dataList.map(item => item.zvalueQj - item.initialH)
            }
          ];
          break;
      }
      return series;
    },
    handleQuery() {
      this.queryParams.pageNum = 1;
      this.getList();
    },
    resetQuery() {
      this.resetForm("queryForm");
      this.queryParams.pageNum = 1;
      this.queryParams.eqmtId = null;
      this.eqmtOptions = [];
      this.getProjects();
    },
    handleExport() {
      const exportData = this.prepareExportData();
      const worksheet = XLSX.utils.json_to_sheet(exportData);
      const workbook = XLSX.utils.book_new();
      XLSX.utils.book_append_sheet(workbook, worksheet, '设备数据');
      XLSX.writeFile(workbook, `设备数据_${new Date().getTime()}.xlsx`);
    },
    prepareExportData() {
      const data = this.dataList.map(item => {
        return {
          '设备类型': item.eqmtTypeName,
          '设备名称': item.eqmtName,
          '设备编码': item.eqmtCode,
          '采集时间': this.parseTime(item.catchTime, "{y}-{m}-{d} {h}:{i}:{s}"),
          ...(this.searchType === 'WY' && {
            'X位移(mm)': item.xvalueWy,
            'Y位移(mm)': item.yvalueWy,
          }),
          ...(this.searchType === 'LF' && {
            '裂缝(mm)': item.lfValue,
          }),
          ...(this.searchType === 'QJ' && {
            'X角度(°)': item.xvalueQj,
            'Y角度(°)': item.yvalueQj,
            'Z角度(°)': item.zvalueQj,
          }),
          ...(this.searchType === 'YL' && {
            '水位(mm)': item.ylValue,
          }),
        };
      });
      return data;
    },
    toggleChartType() {
      if (this.chartType === 'line') {
        this.chartType = 'bar';
      } else {
        this.chartType = 'line';
      }
      this.renderChart();
    },
  }
}
</script>
<style scoped>
.chart-container {
  width: 100%;
  height: 600px; /* 增加高度 */
  background: #2c3e50;
  border-radius: 8px;
  padding: 20px;
  box-shadow: 0 4px 20px rgba(0,0,0,0.2);
}
</style>
