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
      <el-form-item label="设备名称" prop="eqmtIds">
        <el-select
          v-model="queryParams.eqmtIds"
          multiple
          collapse-tags
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
          <el-table-column label="位移(mm)" align="center" prop="valueWy" v-if="searchType === 'WY'">
            <template slot-scope="scope">
              {{ formatNumber(scope.row.valueWy) }}
            </template>
          </el-table-column>
          <el-table-column label="位移累计变化量(mm)" align="center" v-if="searchType === 'WY'">
            <template slot-scope="scope">
              {{ calculateChange(scope.row.valueWy, scope.row.initialX, 2) }}
            </template>
          </el-table-column>

          <el-table-column label="裂缝累计变化量(mm)" align="center"
                           v-if="searchType === 'LF'">
            <template slot-scope="scope">
              {{calculateChange(scope.row.lfValue, scope.row.initialX, 3)}}
            </template>
          </el-table-column>
          <el-table-column label="裂缝(mm)" align="center" prop="lfValue" v-if="searchType === 'LF'">
            <template slot-scope="scope">
              {{formatNumber(scope.row.lfValue)}}
            </template>
          </el-table-column>

          <el-table-column label="X角度(°)" align="center" prop="xvalueQj" v-if="searchType === 'QJ'"/>
          <el-table-column label="X角度累计变化量(°)" align="center"
                           v-if="searchType === 'QJ'">
            <template slot-scope="scope">
              {{calculateChange(scope.row.xvalueQj, scope.row.initialX, 3)}}
            </template>
          </el-table-column>
          <el-table-column label="Y角度(°)" align="center" prop="yvalueQj" v-if="searchType === 'QJ'"/>
          <el-table-column label="Y角度累计变化量(°)" align="center"
                           v-if="searchType === 'QJ'">
            <template slot-scope="scope">
              {{calculateChange(scope.row.yvalueQj, scope.row.initialY, 3)}}
            </template>
          </el-table-column>
          <el-table-column label="Z角度累计变化量(°)" align="center"
                           v-if="searchType === 'QJ'">
            <template slot-scope="scope">
              {{calculateChange(scope.row.zvalueQj, scope.row.initialH, 3)}}
            </template>
          </el-table-column>
          <el-table-column label="Z角度(°)" align="center" prop="zvalueQj" v-if="searchType === 'QJ'">
            <template slot-scope="scope">
              {{formatNumber(scope.row.zvalueQj)}}
            </template>
          </el-table-column>


          <el-table-column label="水位(mm)" align="center" prop="ylValue" v-if="searchType === 'YL'">
            <template slot-scope="scope">
              {{formatNumber(scope.row.ylValue)}}
            </template>
          </el-table-column>
          <el-table-column label="水位累计变化量(mm)" align="center"
                           v-if="searchType === 'YL'">
            <template slot-scope="scope">
              {{calculateChange(scope.row.ylValue, scope.row.initialX, 1)}}
            </template>
          </el-table-column>
          <el-table-column label="采集时间" align="center" prop="catchTime" sortable>
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
        eqmtIds: [],
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
        if (response.rows && response.rows.length > 0) {
          this.queryParams.eqmtTypeId = response.rows[0].id;
          this.handleEmqtTypeChange(this.queryParams.eqmtTypeId);
        } else {
          this.queryParams.eqmtTypeId = null;
          this.queryParams.eqmtIds = [];
          this.eqmtOptions = [];
        }
      });
    },
    getList() {
      this.loading = true;
      let eqmtTypeItem = this.eqmtTypeOptions.find(item => item.id === this.queryParams.eqmtTypeId);

      if (eqmtTypeItem) {
        this.searchType = eqmtTypeItem.eqmtTypeSymbol;
      }

      const params = {
        ...this.queryParams,
        eqmtIds: this.queryParams.eqmtIds
      };

      listRealTime(params).then(response => {
        this.dataList = response.rows.map(row => ({
          ...row,
          valueWy: row.valueWy !== undefined ? Number(row.valueWy) : null,
          initialX: row.initialX !== undefined ? Number(row.initialX) : null,
          zvalueQj: row.zvalueQj !== undefined ? Number(row.zvalueQj) : null,
          lfValue: row.lfValue !== undefined ? Number(row.lfValue) : null,
          ylValue: row.ylValue !== undefined ? Number(row.ylValue) : null,
          initialH: row.initialH !== undefined ? Number(row.initialH) : null
        }));

        this.total = response.total;
        this.loading = false;
        this.updateChartData();
      });
    },
    handleEqmtChange() {
      if (this.queryParams.eqmtIds.length > 0) {
        this.getList();
      } else {
        this.dataList = [];
        if (this.myChart) {
          this.myChart.dispose();
          this.myChart = null;
        }
      }
    },
    handleEmqtTypeChange(val) {
      this.queryParams.eqmtIds = [];
      this.eqmtOptions = [];

      if (val) {
        let eqmtTypeItem = this.eqmtTypeOptions.find(item => item.id === val);
        if (eqmtTypeItem) {
          this.searchType = eqmtTypeItem.eqmtTypeSymbol;
        }

        listAqyEquipment({ eqmtTypeId: val }).then(response => {
          this.eqmtOptions = response.rows;
          if (this.eqmtOptions && this.eqmtOptions.length > 0) {
            this.queryParams.eqmtIds = [this.eqmtOptions[0].id];
            this.getList();
          } else {
            this.dataList = [];
          }
        });

        if (this.myChart) {
          this.myChart.dispose();
          this.myChart = null;
        }
      }
    },
    updateChartData() {
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
      if (!chartDom) return;

      if (this.myChart) {
        this.myChart.dispose();
      }

      this.myChart = echarts.init(chartDom);
      const xAxisData = this.getXAxisData();
      const seriesData = this.getSeriesData();

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
            type: 'cross'
          }
        },
        legend: {
          data: seriesData.map(item => item.name),
          textStyle: {
            color: '#fff'
          },
          top: '5%',
          type: 'scroll',
          pageTextStyle: {
            color: '#fff'
          }
        },
        grid: {
          left: '3%',
          right: '4%',
          bottom: '10%',
          containLabel: true
        },
        xAxis: {
          type: 'category',
          data: xAxisData,
          axisLabel: {
            rotate: 45,
            interval: Math.floor(xAxisData.length / 15),
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
          scale: true,
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
              color: 'rgba(255, 255, 255, 0.1)'
            }
          }
        },
        dataZoom: [
          {
            type: 'slider',
            show: true,
            xAxisIndex: [0],
            start: 0,
            end: 100,
            textStyle: {
              color: '#fff'
            }
          },
          {
            type: 'inside',
            xAxisIndex: [0]
          }
        ],
        series: seriesData.map(series => ({
          ...series,
          showSymbol: false,
          smooth: true,
          lineStyle: {
            width: 2
          }
        }))
      };

      this.myChart.setOption(option);
    },
    getXAxisData() {
      return [...new Set(this.dataList.map(item =>
        this.parseTime(item.catchTime, '{y}-{m}-{d} {h}:{i}:{s}')
      ))].sort((a, b) => new Date(a) - new Date(b));
    },
    getSeriesData() {
      const series = [];
      const uniqueEqmts = [...new Set(this.dataList.map(item => item.eqmtName))];
      const xAxisData = this.getXAxisData();

      const dataMap = {};
      uniqueEqmts.forEach(eqmtName => {
        dataMap[eqmtName] = {};
        this.dataList.forEach(item => {
          if (item.eqmtName === eqmtName) {
            const timeKey = this.parseTime(item.catchTime, '{y}-{m}-{d} {h}:{i}:{s}');
            dataMap[eqmtName][timeKey] = item;
          }
        });
      });

      switch(this.searchType) {
        case 'WY':
          uniqueEqmts.forEach(eqmtName => {
            const data = xAxisData.map(time => {
              const item = dataMap[eqmtName][time];
              return item ? this.calculateChange(item.valueWy, item.initialX, 2) : null;
            });
            series.push({
              name: `${eqmtName}-位移累计变化量(mm)`,
              type: this.chartType,
              data: data,
              connectNulls: true
            });
          });
          break;

        case 'LF':
          uniqueEqmts.forEach(eqmtName => {
            const data = xAxisData.map(time => {
              const item = dataMap[eqmtName][time];
              return item ? this.calculateChange(item.lfValue, item.initialX, 3) : null;
            });
            series.push({
              name: `${eqmtName}-裂缝累计变化量(mm)`,
              type: this.chartType,
              data: data,
              connectNulls: true
            });
          });
          break;

        case 'YL':
          uniqueEqmts.forEach(eqmtName => {
            const data = xAxisData.map(time => {
              const item = dataMap[eqmtName][time];
              return item ? this.calculateChange(item.ylValue, item.initialX, 1) : null;
            });
            series.push({
              name: `${eqmtName}-水位累计变化量(mm)`,
              type: this.chartType,
              data: data,
              connectNulls: true
            });
          });
          break;

        case 'QJ':
          uniqueEqmts.forEach(eqmtName => {
            const xData = xAxisData.map(time => {
              const item = dataMap[eqmtName][time];
              return item ? this.calculateChange(item.xvalueQj, item.initialX, 3) : null;
            });
            const yData = xAxisData.map(time => {
              const item = dataMap[eqmtName][time];
              return item ? this.calculateChange(item.yvalueQj, item.initialY, 3) : null;
            });
            const zData = xAxisData.map(time => {
              const item = dataMap[eqmtName][time];
              return item ? this.calculateChange(item.zvalueQj, item.initialH, 3) : null;
            });

            series.push(
              {
                name: `${eqmtName}-X角度累计变化量(°)`,
                type: this.chartType,
                data: xData,
                connectNulls: true
              },
              {
                name: `${eqmtName}-Y角度累计变化量(°)`,
                type: this.chartType,
                data: yData,
                connectNulls: true
              },
              {
                name: `${eqmtName}-Z角度累计变化量(°)`,
                type: this.chartType,
                data: zData,
                connectNulls: true
              }
            );
          });
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
      this.queryParams.eqmtIds = [];
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
    formatNumber(value, decimals = 3) {
      if (value === undefined || value === null || isNaN(Number(value))) {
        return '--';
      }
      return Number(value).toFixed(decimals);
    },
    calculateChange(currentValue, initialValue, decimals = 2) {
      if (currentValue === undefined || currentValue === null ||
        initialValue === undefined || initialValue === null ||
        isNaN(Number(currentValue)) || isNaN(Number(initialValue))) {
        return '--';
      }

      const current = Number(currentValue);
      const initial = Number(initialValue);
      const change = current - initial;

      return isNaN(change) ? '--' : change.toFixed(decimals);
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

::v-deep .el-select {
.el-select__tags {
  max-width: calc(100% - 30px);
}
}
</style>
