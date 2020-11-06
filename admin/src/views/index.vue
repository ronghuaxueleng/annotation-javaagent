<template>
  <div class="dashboard-container">
    <div class="save-button">
      <el-button type="success" @click="save">保存</el-button>
    </div>
    <div class="content-container">
      <el-tabs :tab-position="'left'">
        <el-tab-pane v-for="(paths, controller) in links" :key="controller" :label="paths.controllerName">
          <div>
            <span>
              模块名称：
            </span>
            <span>
              <el-input
                v-model="paths.controllerName"
                class="controller-name"
              />
            </span>
          </div>
          <el-tabs>
            <el-tab-pane v-for="path in paths.methodList" :key="path.code" :label="path.methondName">
              <el-form class="method-form" :inline="true" :model="path" label-width="100px">
                <el-form-item label="请求链接：">
                  <span>
                    {{ path.fullUrl }}
                  </span>
                </el-form-item>
                <div>
                  <el-form-item label="方法：">
                    <span>
                      {{ path.method }}
                    </span>
                  </el-form-item>
                  <el-form-item class="method-name-input" label="方法名称：">
                    <el-input
                      v-model="path.methondName"
                      size="small"
                    />
                  </el-form-item>
                </div>
                <div>
                  <el-form-item label="请求参数：" class="params-table">
                    <span style="color: blue;font-weight: 600;">点击中文名进行编辑</span>
                    <el-table :data="path.methodParams" :row-class-name="getRowClass">
                      <el-table-column type="expand">
                        <template slot-scope="props">
                          <el-table :data="entitys[props.row.type]">
                            <el-table-column prop="name" label="参数" align="center" />
                            <el-table-column label="中文名" align="center">
                              <editeable-cell
                                v-model="row.cnName"
                                slot-scope="{row}"
                                :show-input="row.editMode"
                              >
                                <span slot="content">{{ row.cnName }}</span>
                              </editeable-cell>
                            </el-table-column>
                            <el-table-column prop="genericType" label="类型" align="center" />
                          </el-table>
                        </template>
                      </el-table-column>
                      <el-table-column prop="name" label="参数" align="center" />
                      <el-table-column label="中文名" align="center">
                        <editeable-cell
                          v-model="row.cnName"
                          slot-scope="{row}"
                          :show-input="row.editMode"
                        >
                          <span slot="content">{{ row.cnName }}</span>
                        </editeable-cell>
                      </el-table-column>
                      <el-table-column prop="type" label="类型" align="center" show-overflow-tooltip />
                    </el-table>
                  </el-form-item>
                </div>
              </el-form>
            </el-tab-pane>
          </el-tabs>
        </el-tab-pane>
      </el-tabs>
    </div>
  </div>
</template>

<script>
import editeableCell from '@/components/editeableCell'
import { getAllUrls, save } from '@/api/api'

export default {
  components: {
    editeableCell
  },
  data() {
    return {
      links: {},
      entitys: {},
      controller: '',
      annotations: []
    }
  },
  created() {
    const that = this
    getAllUrls().then(response => {
      if (response.status === 200 && response.data.code === 200) {
        const data = response.data.data
        that.links = data.controllers
        that.entitys = data.entitys
      }
    })
  },
  methods: {
    getRowClass: function(row, index) {
      if (!row.row.baseType) {
        return ''
      } else {
        return 'hide-expand'
      }
    },
    buildeAnnotation() {
      for (const controllerName in this.links) {
        const link = this.links[controllerName]
        const anno = {
          'fullClassName': link.fullClassName,
          'annotations': [{
            'name': 'io.swagger.annotations.Api',
            'attrs': [{ 'attrValue': [link.controllerName], 'attrName': 'tags' }]
          }],
          'methodList': []
        }

        const methodList = link.methodList

        for (const index in methodList) {
          const method = methodList[index]
          const methodAnnotations = [
            {
              'name': 'io.swagger.annotations.ApiOperation',
              'attrs': [{ 'attrValue': method.methondName, 'attrName': 'value' }]
            }
          ]

          const paramAnnotations = {
            'name': 'io.swagger.annotations.ApiImplicitParams',
            'attrs': [{
              'attrValue': [],
              'attrName': 'value'
            }]
          }
          for (const pIndex in method.methodParams) {
            const methodParam = method.methodParams[pIndex]
            paramAnnotations['attrs'][0]['attrValue'].push({
              'name': 'io.swagger.annotations.ApiImplicitParam',
              'attrs': [
                { 'attrValue': methodParam.name, 'attrName': 'name' },
                { 'attrValue': methodParam.cnName, 'attrName': 'value' }
              ]
            })

            if (!methodParam.baseType) {
              const entityaAnno = {
                'fullClassName': methodParam.type,
                'annotations': [{
                  'name': 'io.swagger.annotations.ApiModel',
                  'attrs': [{ 'attrValue': methodParam.cnName, 'attrName': 'value' }]
                }],
                'fieldList': []
              }

              const fields = this.entitys[methodParam.type]
              for (const pIndexKey in fields) {
                const field = fields[pIndexKey]
                entityaAnno['fieldList'].push(
                  {
                    'field': field.name,
                    'annotations': [
                      {
                        'name': 'io.swagger.annotations.ApiModelProperty',
                        'attrs': [{ 'attrValue': field.cnName, 'attrName': 'value' }]
                      }
                    ]
                  }
                )
              }
              this.annotations.push(entityaAnno)
            }
          }

          if (paramAnnotations['attrs'][0]['attrValue'].length > 0) {
            methodAnnotations.push(paramAnnotations)
          }

          anno['methodList'].push({
            'method': method.method,
            'annotations': methodAnnotations
          })
        }
        this.annotations.push(anno)
      }
    },
    save() {
      const that = this
      this.buildeAnnotation()
      save(this.annotations).then(response => {
        if (response.status === 200 && response.data.code === 200) {
          that.$message({
            message: '保存成功',
            type: 'success'
          })
        } else {
          that.$message.error('保存失败')
        }
      }).catch(function() {
        that.$message.error('保存失败')
      })
    }
  }
}
</script>

<style lang="stylus">

.hide-expand > .el-table__expand-column {
  visibility: hidden
}

.dashboard-container {
  background: #fff;

  .save-button {
    text-align: right;
    padding: 10px;
  }

  .content-container {
    margin-top: 10px;

    .el-tabs {
      font-weight: 600;
    }

    .controller-name {
      width: 770px;
    }

    .method-form {
      .method-name-input {
        width: 700px;

        .el-form-item__content {
          width: 600px;
        }
      }

      .params-table {
        .el-form-item__content {
          .el-table {
            width: 815px;

            .el-table__body-wrapper {
              .el-table__body {
                .edit-cell {
                  min-width: 130px;
                  height: 50px;
                  line-height: 50px;
                }

                .el-select {
                  .el-input__inner {
                    min-width: 110px;
                    height: 28px;
                  }

                  .el-input__icon {
                    line-height: 28px;
                  }
                }
              }
            }
          }
        }
      }

    }

  }
}
</style>
