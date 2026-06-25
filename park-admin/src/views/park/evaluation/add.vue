<template>
  <div class="evaluation-add">
    <!-- 顶部标题栏 -->
    <div class="detail-header">
      <div class="header-left">
        <span class="title">{{ pageTitle }}</span>
      </div>
    </div>

    <!-- 返回与标题 -->
    <div class="detail-nav">
      <div class="back-btn" @click="goBack">
        <i class="el-icon-back" />
        <span>返回列表</span>
      </div>
    </div>

    <!-- 左右布局：分类列表 + 内容区 -->
    <div class="add-layout">
      <!-- 左侧分类选项列表 -->
      <div class="category-sidebar">
        <div
          v-for="category in categories"
          :key="category.id"
          :class="['category-item', {
            'category-item--active': activeCategory === category.id,
            'category-item--completed': completedCategories.has(category.id),
            'category-item--viewed': viewedCategories.has(category.id),
            'category-item--readonly': isViewMode
          }]"
          @click="handleCategoryClick(category)"
        >
          <span class="category-index">{{ category.id }}.</span>
          <span class="category-name">{{ category.name }}</span>
          <i v-if="completedCategories.has(category.id)" class="el-icon-check category-check"></i>
          <i v-else class="el-icon-arrow-right category-arrow"></i>
        </div>
      </div>

      <!-- 右侧内容区 -->
      <div class="category-content">
        <!-- 基础指标 -->
        <div v-if="activeCategory === 1" class="content-panel">
          <div class="panel-header">
            <div class="panel-title-bar">
              <div class="title-decoration"></div>
              <span class="panel-title">基础指标</span>
            </div>
          </div>
          <div class="panel-body">
            <div class="indicator-item">
              <div class="indicator-number">①</div>
              <div class="indicator-desc">
                评价年度内参评园区需符合《杭州市升级版小微企业园建设和管理工作指引(试行)》明确的小微企业园认定条件，不具备的直接列D档。
              </div>
            </div>
            <el-radio-group v-model="form.basicAcknowledged" class="acknowledge-radio" :disabled="isViewMode">
              <el-radio label="known">我已知晓</el-radio>
            </el-radio-group>
          </div>
          <div class="panel-footer">
            <el-button size="small" @click="handlePrev" :disabled="activeCategory === 1 || isViewMode">上一步</el-button>
            <el-button type="primary" size="small" @click="handleNext" :disabled="isViewMode">下一步</el-button>
          </div>
        </div>

        <!-- 产业发展 -->
        <div v-else-if="activeCategory === 2" class="content-panel">
          <div class="panel-header">
            <div class="panel-title-bar">
              <div class="title-decoration"></div>
              <span class="panel-title">产业发展</span>
            </div>
          </div>
          <div class="panel-body">
            <div class="indicator-list">
              <div class="indicator-text">① 园区产业链核五大产业生态圈（智能智联、生物医药、高端装备、新材料和绿色能源五大产业生态圈）集聚发展，并以此形成主导产业的，得5分。</div>
              <div class="indicator-text">② 园区主导产业明确，效益突出，评价年度内主导产业产值（税收）及入驻企业数占比均超过50%，两项占比在50%的得基础上每增加10%，得10分。</div>
              <div class="indicator-text">③ 评价年度内园区主导产业内企业不少于10家，产值占比不低于60%，产值占比在60%的基础上每增加10%，得10分。</div>
              <div class="indicator-text">④ 评价年度内园区注册企业列表名单。</div>
            </div>
            <div class="action-bar">
              <el-button type="primary" plain size="small" @click="handleImport" :disabled="isViewMode">导入数据</el-button>
              <el-button type="primary" plain size="small" @click="handleDownloadTemplate">下载模板</el-button>
            </div>
            <el-table :data="enterpriseList" border stripe size="small" class="enterprise-table">
              <el-table-column type="index" label="序  号" width="80" align="center" />
              <el-table-column prop="parkName" label="园区名称" min-width="180" align="center" />
              <el-table-column prop="enterpriseName" label="入驻企业名称" min-width="220" align="center" />
              <el-table-column prop="creditCode" label="统一社会信用代码" min-width="200" align="center" />
              <el-table-column prop="settledTime" label="入驻起止时间" min-width="150" align="center" />
              <el-table-column prop="enterpriseAddress" label="企业注册地" min-width="280" align="center" show-overflow-tooltip />
            </el-table>
          </div>
          <div class="panel-footer">
            <el-button size="small" @click="handlePrev" :disabled="isViewMode">上一步</el-button>
            <el-button type="primary" size="small" @click="handleNext" :disabled="isViewMode">下一步</el-button>
          </div>
        </div>

        <!-- 企业培育 -->
        <div v-else-if="activeCategory === 3" class="content-panel">
          <div class="panel-header">
            <div class="panel-title-bar">
              <div class="title-decoration"></div>
              <span class="panel-title">企业培育</span>
            </div>
          </div>
          <div class="panel-body">
            <div class="indicator-list">
              <div class="indicator-text">① 园区内存量的规模以上工业企业每家得1分；评价年度内园区 每新增1家规模以上工业企业得2分；评价年度内规模以上工业企业退到规下的，每退减1家减1分。</div>
              <div class="indicator-text">② 评价年度内园区内企业每新增一家国家级制造业单项冠军得4分；每新增一家上市企业得3分；每新增一家国家级专精特新小巨人或省级隐形冠军企业得2分；每新增一家省专精特新中小企业或国家高新技术企业得1分；每新增一家创新型中小企业得0.5分。同一企业不重复计算。</div>
              <div class="indicator-text">③ 园区建立专属产业基金，评价年度内园区内企业"投早投小投创新"案例，每新增1个得1分。</div>
            </div>

            <!-- 批量上传按钮 -->
            <div class="batch-upload-bar">
              <el-tooltip content="支持格式:.docx, .xls, .xlsx, .pdf, .png, .jpg, .jpeg | 文件大小限制:50MB" placement="top">
                <el-button type="primary" plain size="small" @click="handleCultivationBatchUpload" :disabled="isViewMode">上传附件</el-button>
              </el-tooltip>
            </div>

            <!-- 文件列表（上传后显示） -->
            <div v-if="cultivationFiles.length > 0" class="cultivation-list-wrapper">
              <div v-for="(item, index) in cultivationFiles" :key="index" class="cultivation-item">
                <!-- 项目名称（可编辑） -->
                <div class="cultivation-header">
                  <span class="tech-label">所属项目：</span>
                  <el-input v-model="item.projectName" size="small" class="tech-project-input" placeholder="请输入项目名称" :disabled="isViewMode"></el-input>
                  <i v-if="!isViewMode" class="el-icon-edit-outline edit-icon"></i>
                  <span v-if="!isViewMode" class="action-link delete-link" @click="handleDeleteCultivation(index)">删除</span>
                </div>
                <!-- 文件预览删除区域 -->
                <div class="tech-file-area">
                  <div class="tech-file-row">
                    <i class="el-icon-document"></i>
                    <el-input v-model="item.fileName" size="small" class="file-name-input" placeholder="请输入文件名" :disabled="isViewMode"></el-input>
                    <span class="action-link preview-link" @click.stop="handlePreview(item)">预览</span>
                    <span v-if="!isViewMode" class="action-link delete-link" @click.stop="handleDeleteCultivationFile(index)">删除</span>
                  </div>
                </div>
              </div>
            </div>
          </div>
          <div class="panel-footer">
            <el-button size="small" @click="handlePrev" :disabled="isViewMode">上一步</el-button>
            <el-button type="primary" size="small" @click="handleNext" :disabled="isViewMode">下一步</el-button>
          </div>
        </div>

        <!-- 科技创新 -->
        <div v-else-if="activeCategory === 4" class="content-panel">
          <div class="panel-header">
            <div class="panel-title-bar">
              <div class="title-decoration"></div>
              <span class="panel-title">科技创新</span>
            </div>
          </div>
          <div class="panel-body">
            <!-- 指标说明 ①②④ -->
            <div class="indicator-list">
              <div class="indicator-text">① 评价年度内园区内企业新增省级及以上平台(类)装备、首批次、首台批次、省级优秀工业新产品、浙江制造精品的，每项得2分。</div>
              <div class="indicator-text">② 评价年度内园区新增国家级、省级、市级企业研发机构的，每 项分别得3分、2分、1分。同一企业(机构)不重复计算。</div>
              <div class="indicator-text">③ 评价年度内园区新引进独立注册的市场及以上科研院所、企业 孵化或检验检测服务等公共事务平台的，每项得2分。同一项 目不重复计算。</div>
              <div class="indicator-text">④ 根据杭州市高层次人才分类目录标准，评价年度内园区企业新认定A、B、C、D类人才的，每人次分别3分、2分、1.5分、1分。</div>
            </div>

            <!-- ① 和 ② 文件列表 -->
            <div class="batch-upload-bar">
              <el-tooltip content="支持格式:.docx, .xls, .xlsx, .pdf, .png, .jpg, .jpeg | 文件大小限制:50MB" placement="top">
                <el-button type="primary" plain size="small" @click="handleTechBatchUpload" :disabled="isViewMode">上传附件</el-button>
              </el-tooltip>
            </div>
            <div v-if="techInnovations.length > 0" class="tech-list-wrapper">
              <div v-for="(item, index) in techInnovations" :key="index" class="tech-item">
                <!-- 项目名称（可编辑） -->
                <div class="tech-header">
                  <span class="tech-label">所属项目：</span>
                  <el-input v-model="item.projectName" size="small" class="tech-project-input" placeholder="请输入项目名称" :disabled="isViewMode"></el-input>
                  <i v-if="!isViewMode" class="el-icon-edit-outline edit-icon"></i>
                  <span v-if="!isViewMode" class="action-link delete-link" @click="handleDeleteTech(index)">删除</span>
                </div>
                <!-- 文件预览删除区域 -->
                <div class="tech-file-area">
                  <div class="tech-file-row">
                    <i class="el-icon-document"></i>
                    <span class="tech-file-name">{{ item.fileName }}</span>
                    <span class="action-link preview-link" @click.stop="handlePreview(item)">预览</span>
                    <span v-if="!isViewMode" class="action-link delete-link" @click.stop="handleDeleteTechFile(index)">删除</span>
                  </div>
                </div>
                <!-- 人才信息 -->
                <div class="tech-info-row">
                  <el-select v-model="item.category" size="small" class="tech-cell category-cell" placeholder="请选择人才类别" :disabled="isViewMode">
                    <el-option label="A类" value="A类"></el-option>
                    <el-option label="B类" value="B类"></el-option>
                    <el-option label="C类" value="C类"></el-option>
                    <el-option label="D类" value="D类"></el-option>
                  </el-select>
                  <el-input v-model="item.name" size="small" class="tech-cell" placeholder="请输入姓名" :disabled="isViewMode"></el-input>
                  <el-date-picker v-model="item.date" type="date" size="small" class="tech-cell" value-format="yyyy-MM-dd" placeholder="选择日期" :disabled="isViewMode"></el-date-picker>
                  <el-input v-model="item.company" size="small" class="tech-cell" placeholder="请输入所属企业" :disabled="isViewMode"></el-input>
                </div>
              </div>
            </div>

            <!-- ⑤ 院所合作文件列表 -->
            <div class="indicator-list" style="margin-top:24px;">
              <div class="indicator-text">⑤ 园区与科研院所建立合作关系，在园区开展科研成果转移转化 并在评价年度形成500万元及以上产出的，每项得1分。</div>
            </div>
            <div class="batch-upload-bar">
              <el-tooltip content="支持格式:.docx, .xls, .xlsx, .pdf, .png, .jpg, .jpeg | 文件大小限制:50MB" placement="top">
                <el-button type="primary" plain size="small" @click="handleProjectBatchUpload" :disabled="isViewMode">上传附件</el-button>
              </el-tooltip>
            </div>
            <div v-if="projects.length > 0" class="project-list-wrapper">
              <div v-for="(project, index) in projects" :key="index" class="project-item">
                <!-- 项目名称（可编辑） -->
                <div class="project-header">
                  <span class="tech-label">所属项目：</span>
                  <el-input v-model="project.name" size="small" class="project-name-input" placeholder="请输入项目名称" :disabled="isViewMode"></el-input>
                  <i v-if="!isViewMode" class="el-icon-edit-outline edit-icon"></i>
                  <span v-if="!isViewMode" class="action-link delete-link" @click="handleDeleteProject(index)">删除</span>
                </div>
                <!-- 附件文件列表 -->
                <div class="tech-file-area">
                  <div class="project-file-row">
                    <i class="el-icon-document"></i>
                    <span class="file-name">{{ project.fileName }}</span>
                    <span class="action-link preview-link" @click.stop="handlePreview(project)">预览</span>
                    <span v-if="!isViewMode" class="action-link delete-link" @click.stop="handleDeleteProjectFile(index)">删除</span>
                  </div>
                </div>
              </div>
            </div>
          </div>
          <div class="panel-footer">
            <el-button size="small" @click="handlePrev" :disabled="isViewMode">上一步</el-button>
            <el-button type="primary" size="small" @click="handleNext" :disabled="isViewMode">下一步</el-button>
          </div>
        </div>

        <!-- 服务能力 -->
        <div v-else-if="activeCategory === 5" class="content-panel">
          <div class="panel-header">
            <div class="panel-title-bar">
              <div class="title-decoration"></div>
              <span class="panel-title">服务能力</span>
            </div>
          </div>
          <div class="panel-body">
            <!-- 指标① 助企服务站、一站式代办、党团工会活动 -->
            <div class="indicator-list">
              <div class="indicator-text">① 园区建立助企服务站(工作室)并有明确完善助企服务机制的，得5分；对入园企业项目报批实行一站式全程代办服务的，得5分；按规定组织企业开展党员、团员及工会活动的，得5分。</div>
            </div>
            
            <div class="service-row">
              <!-- 助企服务站建设材料 -->
              <div class="service-col">
                <div class="service-title">助企服务站建设材料</div>
                <el-tooltip content="支持格式:.docx, .xls, .xlsx, .pdf, .png, .jpg, .jpeg | 文件大小限制:50MB" placement="top">
                  <el-button type="default" size="small" class="service-upload-btn" @click="handleServiceUpload('enterpriseService')" :disabled="isViewMode">上传附件</el-button>
                </el-tooltip>
                <div v-if="serviceFiles.enterpriseService.length > 0" class="service-file-list">
                  <div v-for="(file, index) in serviceFiles.enterpriseService" :key="index" class="service-file-item">
                    <i class="el-icon-document"></i>
                    <span class="service-file-name">{{ file.fileName }}</span>
                    <span class="action-link preview-link" @click.stop="handlePreview(file)">预览</span>
                    <span v-if="!isViewMode" class="action-link delete-link" @click.stop="handleDeleteServiceFile('enterpriseService', index)">删除</span>
                  </div>
                </div>
              </div>
              
              <!-- 一站式代办服务材料 -->
              <div class="service-col">
                <div class="service-title">一站式代办服务材料</div>
                <el-tooltip content="支持格式:.docx, .xls, .xlsx, .pdf, .png, .jpg, .jpeg | 文件大小限制:50MB" placement="top">
                  <el-button type="default" size="small" class="service-upload-btn" @click="handleServiceUpload('oneStopService')" :disabled="isViewMode">上传附件</el-button>
                </el-tooltip>
                <div v-if="serviceFiles.oneStopService.length > 0" class="service-file-list">
                  <div v-for="(file, index) in serviceFiles.oneStopService" :key="index" class="service-file-item">
                    <i class="el-icon-document"></i>
                    <span class="service-file-name">{{ file.fileName }}</span>
                    <span class="action-link preview-link" @click.stop="handlePreview(file)">预览</span>
                    <span v-if="!isViewMode" class="action-link delete-link" @click.stop="handleDeleteServiceFile('oneStopService', index)">删除</span>
                  </div>
                </div>
              </div>
            </div>
            
            <!-- 党团工会活动材料 -->
            <div class="service-row">
              <div class="service-col-full">
                <div class="service-title">党团工会活动材料</div>
                <el-tooltip content="支持格式:.docx, .xls, .xlsx, .pdf, .png, .jpg, .jpeg | 文件大小限制:50MB" placement="top">
                  <el-button type="default" size="small" class="service-upload-btn" @click="handleServiceUpload('unionActivity')" :disabled="isViewMode">上传附件</el-button>
                </el-tooltip>
                <div v-if="serviceFiles.unionActivity.length > 0" class="service-file-list">
                  <div v-for="(file, index) in serviceFiles.unionActivity" :key="index" class="service-file-item">
                    <i class="el-icon-document"></i>
                    <span class="service-file-name">{{ file.fileName }}</span>
                    <span class="action-link preview-link" @click.stop="handlePreview(file)">预览</span>
                    <span v-if="!isViewMode" class="action-link delete-link" @click.stop="handleDeleteServiceFile('unionActivity', index)">删除</span>
                  </div>
                </div>
              </div>
            </div>

            <!-- 指标② 园区大脑数字化 -->
            <div class="indicator-list" style="margin-top:24px;">
              <div class="indicator-text">② 建立园区大脑，并获得省级数字化示范园区的，得5分，未获得省级数字化示范园区，但能集聚园区数据资源，集成相关功能模块，利用实时数据优化园区公共资源，实现园区治理智慧化，助力园区运营升级的，得4分；未建立园区大脑的最高得3分，其中，有信息发布平台、数字化建设等独立系统的，可得1分；已实施数字化物业智管理的，可得1分；具有数字化管理专门机构的，可得1分；实现园区安全生产数字化监管的，可得1分。</div>
            </div>
            
            <div class="service-row">
              <div class="service-col-full">
                <div class="service-title">园区大脑数字化相关资料</div>
                <el-tooltip content="支持格式:.docx, .xls, .xlsx, .pdf, .png, .jpg, .jpeg | 文件大小限制:50MB" placement="top">
                  <el-button type="default" size="small" class="service-upload-btn" @click="handleServiceUpload('parkBrain')" :disabled="isViewMode">上传附件</el-button>
                </el-tooltip>
                <div v-if="serviceFiles.parkBrain.length > 0" class="service-file-list">
                  <div v-for="(file, index) in serviceFiles.parkBrain" :key="index" class="service-file-item">
                    <i class="el-icon-document"></i>
                    <span class="service-file-name">{{ file.fileName }}</span>
                    <span class="action-link preview-link" @click.stop="handlePreview(file)">预览</span>
                    <span v-if="!isViewMode" class="action-link delete-link" @click.stop="handleDeleteServiceFile('parkBrain', index)">删除</span>
                  </div>
                </div>
              </div>
            </div>

            <!-- 指标③ 普惠性服务活动、个性化服务活动 -->
            <div class="indicator-list" style="margin-top:24px;">
              <div class="indicator-text">③ 评价年度内开展普惠性服务活动10场以上(每场活动参加企业5家以上),得3分；开展针对性个性化助企服务活动20次以上的，得3分。</div>
            </div>
            
            <div class="service-row">
              <!-- 普惠性服务活动 -->
              <div class="service-col">
                <div class="service-title">普惠性服务活动</div>
                <div class="service-hint">请上传活动"通知+签到+照片"相关资料</div>
                <el-tooltip content="支持格式:.docx, .xls, .xlsx, .pdf, .png, .jpg, .jpeg | 文件大小限制:50MB" placement="top">
                  <el-button type="default" size="small" class="service-upload-btn" @click="handleServiceUpload('inclusiveService')" :disabled="isViewMode">上传附件</el-button>
                </el-tooltip>
                <div v-if="serviceFiles.inclusiveService.length > 0" class="service-file-list">
                  <div v-for="(file, index) in serviceFiles.inclusiveService" :key="index" class="service-file-item">
                    <i class="el-icon-document"></i>
                    <span class="service-file-name">{{ file.fileName }}</span>
                    <span class="action-link preview-link" @click.stop="handlePreview(file)">预览</span>
                    <span v-if="!isViewMode" class="action-link delete-link" @click.stop="handleDeleteServiceFile('inclusiveService', index)">删除</span>
                  </div>
                </div>
              </div>
              
              <!-- 个性化服务活动 -->
              <div class="service-col">
                <div class="service-title">个性化服务活动</div>
                <div class="service-hint">请上传活动"图片+说明"相关资料</div>
                <el-tooltip content="支持格式:.docx, .xls, .xlsx, .pdf, .png, .jpg, .jpeg | 文件大小限制:50MB" placement="top">
                  <el-button type="default" size="small" class="service-upload-btn" @click="handleServiceUpload('personalizedService')" :disabled="isViewMode">上传附件</el-button>
                </el-tooltip>
                <div v-if="serviceFiles.personalizedService.length > 0" class="service-file-list">
                  <div v-for="(file, index) in serviceFiles.personalizedService" :key="index" class="service-file-item">
                    <i class="el-icon-document"></i>
                    <span class="service-file-name">{{ file.fileName }}</span>
                    <span class="action-link preview-link" @click.stop="handlePreview(file)">预览</span>
                    <span v-if="!isViewMode" class="action-link delete-link" @click.stop="handleDeleteServiceFile('personalizedService', index)">删除</span>
                  </div>
                </div>
              </div>
            </div>

            <!-- 指标④ 园区合作项目 -->
            <div class="indicator-list" style="margin-top:24px;">
              <div class="indicator-text">④ 园区与杭州市范围内的其他小微园形成合作关系，进行管理服务输出，评价年度内取得产业、科创合作3个以上项目的，得3分。</div>
            </div>
            
            <div class="service-row">
              <div class="service-col-full">
                <div class="service-title">项目名称</div>
                <el-tooltip content="支持格式:.docx, .xls, .xlsx, .pdf, .png, .jpg, .jpeg | 文件大小限制:50MB" placement="top">
                  <el-button type="default" size="small" class="service-upload-btn" @click="handleServiceUpload('cooperationProject')" :disabled="isViewMode">上传附件</el-button>
                </el-tooltip>
                <div v-if="serviceFiles.cooperationProject.length > 0" class="service-file-list">
                  <div v-for="(file, index) in serviceFiles.cooperationProject" :key="index" class="service-file-item">
                    <i class="el-icon-document"></i>
                    <span class="service-file-name">{{ file.fileName }}</span>
                    <span class="action-link preview-link" @click.stop="handlePreview(file)">预览</span>
                    <span v-if="!isViewMode" class="action-link delete-link" @click.stop="handleDeleteServiceFile('cooperationProject', index)">删除</span>
                  </div>
                </div>
              </div>
            </div>
          </div>
          <div class="panel-footer">
            <el-button size="small" @click="handlePrev" :disabled="isViewMode">上一步</el-button>
            <el-button type="primary" size="small" @click="handleNext" :disabled="isViewMode">下一步</el-button>
          </div>
        </div>

        <!-- 效益产出 -->
        <div v-else-if="activeCategory === 6" class="content-panel">
          <div class="panel-header">
            <div class="panel-title-bar">
              <div class="title-decoration"></div>
              <span class="panel-title">效益产出</span>
            </div>
          </div>
          <div class="panel-body">
            <div class="indicator-list">
              <div class="indicator-text">① 评价年度亩均税收达到全市生产性服务类园区平均水平：1.5-2倍得2分；2-2.5倍得4分；2.5-3倍得6分；3-3.5倍得8分；3.5倍及以上得10分。</div>
              <div class="indicator-text">② 评价年度亩均产出达到全市生产性服务类园区平均水平：1.5-2倍得2分；2-2.5倍得4分；2.5-3倍得6分；3-3.5倍得8分；3.5倍及以上得10分。</div>
              <div class="indicator-text indicator-text-bold">③ 贯彻落实集约发展理念，通过改造提升实现工业上楼、效益提升的，得5分。</div>
            </div>
            <div class="batch-upload-bar">
              <el-tooltip content="支持格式:.docx, .xls, .xlsx, .pdf, .png, .jpg, .jpeg | 文件大小限制:50MB" placement="top">
                <el-button type="primary" plain size="small" @click="handleBenefitUpload" :disabled="isViewMode">上传附件</el-button>
              </el-tooltip>
            </div>
            <div v-if="benefitFiles.length > 0" class="service-file-list">
              <div v-for="(file, index) in benefitFiles" :key="index" class="service-file-item">
                <i class="el-icon-document"></i>
                <span class="service-file-name">{{ file.fileName }}</span>
                <span class="action-link preview-link" @click.stop="handlePreview(file)">预览</span>
                <span v-if="!isViewMode" class="action-link delete-link" @click.stop="handleDeleteBenefitFile(index)">删除</span>
              </div>
            </div>
          </div>
          <div class="panel-footer">
            <el-button size="small" @click="handlePrev" :disabled="isViewMode">上一步</el-button>
            <el-button type="primary" size="small" @click="handleNext" :disabled="isViewMode">下一步</el-button>
          </div>
        </div>

        <!-- 安全生产 -->
        <div v-else-if="activeCategory === 7" class="content-panel">
          <div class="panel-header">
            <div class="panel-title-bar">
              <div class="title-decoration"></div>
              <span class="panel-title">安全生产</span>
            </div>
          </div>
          <div class="panel-body">
            <div class="indicator-list">
              <div class="indicator-text">① 未落实《杭州市小微企业园安全管理通则》要求，经查实的，扣2分；</div>
              <div class="indicator-text">② 未签订消防安全责任书的，扣2分；</div>
              <div class="indicator-text">③ 未落实培训、演练要求的，扣2分；</div>
              <div class="indicator-text">④ 消防设施器材不完整或过期的，扣2分；</div>
              <div class="indicator-text">⑤ 存在安全隐患被省、市主管部门通报的，每次扣2分。</div>
              <div class="indicator-text">⑥ 近一年内发生较大以上的安全生产事故或较大影响的社会事件，园区安全生产隐患未按期整改的，直接列D档。</div>
            </div>
          </div>
          <div class="panel-footer">
            <el-button size="small" @click="handlePrev" :disabled="isViewMode">上一步</el-button>
            <el-button type="primary" size="small" @click="handleNext" :disabled="isViewMode">下一步</el-button>
          </div>
        </div>

        <!-- 其他 -->
        <div v-else-if="activeCategory === 8" class="content-panel">
          <div class="panel-header">
            <div class="panel-title-bar">
              <div class="title-decoration"></div>
              <span class="panel-title">其他</span>
            </div>
          </div>
          <div class="panel-body">
            <div class="indicator-list">
              <div class="indicator-text">① 园区在浙江省小微企业园信息管理系统中未按时完整报送小微企业园季度运行数据，少报一次减2分；年度数据未报的直接列D档。</div>
              <div class="indicator-text">② 申报材料、报送数据作假的，运营管理机构被列入杭州市公共信用信息平台失信联合惩戒对象名单的，直接列D档。</div>
              <div class="indicator-text">③ 评价年度内有媒体负面报道，经查实并造成较大影响的，扣6分。</div>
              <div class="indicator-text">④ 评价年度承诺函。</div>
            </div>
            
            <div class="promise-template">
              <div class="promise-template-header">
                <i class="el-icon-document"></i>
                <span class="promise-template-name">评价年度承诺函模板.docx</span>
                <span class="action-link preview-link" @click="handlePreviewPromiseTemplate">预览</span>
              </div>
            </div>
            
            <div class="batch-upload-bar" style="margin-top:24px;">
              <el-tooltip content="支持格式:.docx, .xls, .xlsx, .pdf, .png, .jpg, .jpeg | 文件大小限制:50MB" placement="top">
                <el-button type="primary" plain size="small" @click="handleOtherUpload" :disabled="isViewMode">上传附件</el-button>
              </el-tooltip>
            </div>
            <div v-if="otherFiles.length > 0" class="service-file-list">
              <div v-for="(file, index) in otherFiles" :key="index" class="service-file-item">
                <i class="el-icon-document"></i>
                <span class="service-file-name">{{ file.fileName }}</span>
                <span class="action-link preview-link" @click.stop="handlePreview(file)">预览</span>
                <span v-if="!isViewMode" class="action-link delete-link" @click.stop="handleDeleteOtherFile(index)">删除</span>
              </div>
            </div>
          </div>
          <div class="panel-footer">
            <el-button size="small" @click="handlePrev" :disabled="isViewMode">上一步</el-button>
            <el-button v-if="!isViewMode" type="primary" plain size="small" @click="handleSaveDraft" :loading="saveLoading">保存草稿</el-button>
            <el-button v-if="!isViewMode" type="primary" size="small" @click="handleSubmit" :loading="submitLoading">提交评价</el-button>
          </div>
        </div>
      </div>
    </div>

    <!-- 隐藏的文件输入框 -->
    <input ref="baseFileInput" type="file" multiple style="display:none" @change="onBaseFileChange" />
    <input ref="techFileInput" type="file" multiple style="display:none" @change="onTechFileChange" />
    <input ref="projectFileInput" type="file" multiple style="display:none" @change="onProjectFileChange" />
    <input ref="cultivationFileInput" type="file" multiple style="display:none" @change="onCultivationFileChange" />
    <input ref="industryFileInput" type="file" style="display:none" @change="onIndustryFileChange" accept=".xlsx,.xls" />
    <!-- 服务能力隐藏文件输入框 -->
    <input ref="serviceEnterpriseInput" type="file" multiple style="display:none" @change="(e) => onServiceFileChange(e, 'enterpriseService')" />
    <input ref="serviceOneStopInput" type="file" multiple style="display:none" @change="(e) => onServiceFileChange(e, 'oneStopService')" />
    <input ref="serviceUnionInput" type="file" multiple style="display:none" @change="(e) => onServiceFileChange(e, 'unionActivity')" />
    <input ref="serviceParkBrainInput" type="file" multiple style="display:none" @change="(e) => onServiceFileChange(e, 'parkBrain')" />
    <input ref="serviceInclusiveInput" type="file" multiple style="display:none" @change="(e) => onServiceFileChange(e, 'inclusiveService')" />
    <input ref="servicePersonalizedInput" type="file" multiple style="display:none" @change="(e) => onServiceFileChange(e, 'personalizedService')" />
    <input ref="serviceCooperationInput" type="file" multiple style="display:none" @change="(e) => onServiceFileChange(e, 'cooperationProject')" />
    <input ref="benefitFileInput" type="file" multiple style="display:none" @change="onBenefitFileChange" />
    <input ref="otherFileInput" type="file" multiple style="display:none" @change="onOtherFileChange" />
    <!-- 文件预览弹窗 -->
    <FilePreview
      :visible.sync="previewVisible"
      :file-url="previewFileUrl"
      :file-name="previewFileName"
    />

    <!-- 导入错误提示弹窗 -->
    <el-dialog
      title="导入数据校验失败"
      :visible.sync="importErrorsVisible"
      width="600px"
      :close-on-click-modal="false"
    >
      <div class="import-error-list">
        <div v-for="(error, index) in importErrors" :key="index" class="import-error-item">
          <span class="error-row">第{{ error.rowNum }}行</span>
          <span class="error-enterprise">{{ error.enterpriseName || '(空)' }}</span>
          <span class="error-message">{{ error.errorMessage }}</span>
        </div>
      </div>
      <span slot="footer" class="dialog-footer">
        <el-button @click="closeImportErrors">确定</el-button>
      </span>
    </el-dialog>
  </div>
</template>

<script>
import FilePreview from '@/components/FilePreview.vue'
import {
  getTechInnovationList,
  addTechInnovation,
  updateTechInnovation,
  deleteTechInnovation,
  getTechProjectList,
  addTechProject,
  updateTechProject,
  deleteTechProject,
  getCultivationRecordList,
  addCultivationRecord,
  updateCultivationRecord,
  deleteCultivationRecord,
  batchSaveTechInnovation,
  batchSaveTechProject,
  batchSaveCultivationRecord,
  uploadFile,
  deleteFile,
  downloadTemplate,
  uploadIndustryDevelopmentData
} from '@/api/tech-innovation'
import { getEvaluationById, addEvaluation, updateEvaluation, submitEvaluation } from '@/api/evaluation'
import { mapGetters } from 'vuex'

export default {
  name: 'EvaluationAdd',
  components: {
    FilePreview
  },
  data() {
    return {
      activeCategory: 1,
      categories: [
        { id: 1, name: '基础指标' },
        { id: 2, name: '产业发展' },
        { id: 3, name: '企业培育' },
        { id: 4, name: '科技创新' },
        { id: 5, name: '服务能力' },
        { id: 6, name: '效益产出' },
        { id: 7, name: '安全生产' },
        { id: 8, name: '其他' }
      ],
      form: {
        basicAcknowledged: ''
      },
      enterpriseList: [],
      importLoading: false,
      importErrors: [],
      importErrorsVisible: false,
      cultivationFiles: [],
      techInnovations: [],
      projects: [],
      serviceFiles: {
        enterpriseService: [],
        oneStopService: [],
        unionActivity: [],
        parkBrain: [],
        inclusiveService: [],
        personalizedService: [],
        cooperationProject: []
      },
      previewVisible: false,
      previewFileUrl: '',
      previewFileName: '',
      evaluationData: {},
      completedCategories: new Set(),
      viewedCategories: new Set(),
      evaluationId: null,
      evaluationYear: null,
      isEditMode: false,
      isViewMode: false,
      fileSections: [],
      benefitFiles: [],
      otherFiles: [],
      submitLoading: false,
      saveLoading: false
    }
  },
  computed: {
    ...mapGetters(['userInfo']),
    pageTitle() {
      if (this.isViewMode) {
        return `${this.evaluationYear}年评价查看`
      }
      if (this.isEditMode) {
        return `${this.evaluationYear}年评价修改`
      }
      return `${this.evaluationYear}年新增评价`
    }
  },
  created() {
    this.initFromParams()
  },
  methods: {
    initFromParams() {
      const params = this.$route.query
      if (params.view === '1') {
        this.isViewMode = true
        // 查看模式下默认第一个分类为已读
        this.viewedCategories.add(1)
        if (params.id) {
          this.evaluationId = parseInt(params.id)
        }
      } else if (params.id) {
        this.evaluationId = parseInt(params.id)
        this.isEditMode = true
      }
      if (params.year) {
        this.evaluationYear = parseInt(params.year)
      } else {
        this.evaluationYear = new Date().getFullYear()
      }
      if ((this.isEditMode || this.isViewMode) && this.evaluationId) {
        this.loadEvaluationData()
      }
    },
    async loadEvaluationData() {
      try {
        const res = await getEvaluationById(this.evaluationId)
        const data = res.data
        if (this.isEditMode && data && data.status !== 0) {
          this.$message.warning('只能修改未提交的评价记录')
          this.$router.push('/park/evaluation')
          return
        }
        // 回显基础指标确认状态
        if (data && data.parkExtraData) {
          try {
            const extra = JSON.parse(data.parkExtraData)
            if (extra.basicAcknowledged) {
              this.form.basicAcknowledged = extra.basicAcknowledged
            }
            // serviceFiles 是对象（{ enterpriseService: [], ... }），不能用 Array.isArray 判断
            if (extra.serviceFiles && typeof extra.serviceFiles === 'object') {
              this.serviceFiles = Object.assign({}, this.serviceFiles, extra.serviceFiles)
            }
            if (Array.isArray(extra.benefitFiles)) {
              this.benefitFiles = extra.benefitFiles
            }
            if (Array.isArray(extra.otherFiles)) {
              this.otherFiles = extra.otherFiles
            }
            if (Array.isArray(extra.fileSections)) {
              this.fileSections = extra.fileSections
            }
          } catch (e) {
            console.warn('解析 parkExtraData 失败', e)
          }
        }
        // 并行加载子表数据
        const [techRes, projectRes, cultRes] = await Promise.all([
          getTechInnovationList(this.evaluationId).catch(() => ({ data: [] })),
          getTechProjectList(this.evaluationId).catch(() => ({ data: [] })),
          getCultivationRecordList(this.evaluationId).catch(() => ({ data: [] }))
        ])
        this.techInnovations = techRes.data || []
        this.projects = projectRes.data || []
        this.cultivationFiles = cultRes.data || []
        // 回显产业发展企业列表（后端按 parkId 关联返回 enterprises）
        if (Array.isArray(data.enterprises)) {
          this.enterpriseList = data.enterprises
        }
      } catch (e) {
        console.error('加载评价数据失败', e)
      }
    },
    // 判断某流程是否已完成操作
    isStepCompleted(categoryId) {
      switch (categoryId) {
        case 1:
          return this.form.basicAcknowledged === 'known'
        case 2:
          return this.enterpriseList.length > 0
        case 3:
          return this.cultivationFiles.length > 0
        case 4:
          return this.techInnovations.length > 0 || this.projects.length > 0
        case 5:
          return Object.values(this.serviceFiles).some(arr => arr.length > 0)
        case 6:
        case 7:
        case 8:
          return true
        default:
          return false
      }
    },
    // 获取流程未完成的提示信息
    getStepReminder(categoryId) {
      const reminders = {
        1: '请确认已知晓基础指标内容',
        2: '请导入产业发展数据',
        3: '请上传企业培育相关附件',
        4: '请上传科技创新相关附件',
        5: '请上传服务能力相关附件'
      }
      return reminders[categoryId] || '请完成当前流程操作'
    },
    // 检查当前流程并标记完成状态，返回是否允许跳转
    checkAndMarkStep() {
      const currentId = this.activeCategory
      const isCompleted = this.isStepCompleted(currentId)
      if (isCompleted) {
        this.completedCategories.add(currentId)
      }
      return isCompleted
    },
    handleCategoryClick(category) {
      const currentId = this.activeCategory
      const targetId = category.id
      if (currentId === targetId) return

      if (this.isViewMode) {
        // 查看模式下：标记当前和目标分类为已读，不弹出操作提示
        this.viewedCategories.add(currentId)
        this.viewedCategories.add(targetId)
      } else {
        const isCompleted = this.checkAndMarkStep()
        if (!isCompleted && this.isStepNeedOperation(currentId)) {
          this.$message.warning(this.getStepReminder(currentId))
        }
      }

      this.activeCategory = targetId
      this.previewVisible = false
    },
    // 判断流程是否需要操作（1-5需要，6-8不需要）
    isStepNeedOperation(categoryId) {
      return categoryId >= 1 && categoryId <= 5
    },
    goBack() {
      this.previewVisible = false
      this.$router.push('/park/evaluation')
    },
    handlePrev() {
      if (this.activeCategory > 1) {
        this.checkAndMarkStep()
        this.previewVisible = false
        this.activeCategory--
      }
    },
    handleNext() {
      if (this.isViewMode) {
        // 查看模式下：标记当前分类为已读，不弹出操作提示
        this.viewedCategories.add(this.activeCategory)
      } else {
        const isCompleted = this.checkAndMarkStep()
        if (!isCompleted && this.isStepNeedOperation(this.activeCategory)) {
          this.$message.warning(this.getStepReminder(this.activeCategory))
        }
      }
      if (this.activeCategory < 8) {
        this.previewVisible = false
        this.activeCategory++
      }
    },
    // 保存评价记录（新增或修改），返回 evaluationId
    async saveEvaluationRecord() {
      const extraData = {
        basicAcknowledged: this.form.basicAcknowledged,
        serviceFiles: this.serviceFiles,
        benefitFiles: this.benefitFiles,
        otherFiles: this.otherFiles,
        fileSections: this.fileSections
      }
      const saveDTO = {
        parkId: this.userInfo.parkId,
        year: this.evaluationYear,
        parkExtraData: JSON.stringify(extraData)
      }
      if (this.evaluationId) {
        saveDTO.id = this.evaluationId
        await updateEvaluation(saveDTO)
        return this.evaluationId
      } else {
        const res = await addEvaluation(saveDTO)
        const newId = res.data
        this.evaluationId = newId
        this.isEditMode = true
        return newId
      }
    },
    // 批量保存子表数据
    async saveSubTables(evaluationId) {
      await Promise.all([
        batchSaveTechInnovation(evaluationId, this.techInnovations),
        batchSaveTechProject(evaluationId, this.projects),
        batchSaveCultivationRecord(evaluationId, this.cultivationFiles)
      ])
    },
    // 保存草稿
    async handleSaveDraft() {
      this.saveLoading = true
      try {
        const id = await this.saveEvaluationRecord()
        await this.saveSubTables(id)
        this.$message.success('草稿保存成功')
      } catch (e) {
        console.error('保存草稿失败', e)
        this.$message.error('保存失败：' + (e.message || '请稍后重试'))
      } finally {
        this.saveLoading = false
      }
    },
    async handleSubmit() {
      this.saveCurrentStep()
      this.previewVisible = false
      try {
        await this.$confirm('确定提交评价？提交后将不可修改。', '提示', {
          confirmButtonText: '确定',
          cancelButtonText: '取消',
          type: 'warning'
        })
        this.submitLoading = true
        // 1. 保存评价记录
        const id = await this.saveEvaluationRecord()
        // 2. 保存子表数据
        await this.saveSubTables(id)
        // 3. 提交评价
        await submitEvaluation(id)
        this.$message.success('评价提交成功')
        setTimeout(() => {
          this.$router.push('/park/evaluation')
        }, 1500)
      } catch (err) {
        if (err !== 'cancel') {
          console.error('提交失败', err)
          this.$message.error('提交失败：' + (err.message || '请稍后重试'))
        }
      } finally {
        this.submitLoading = false
      }
    },
    saveCurrentStep() {
      this.evaluationData[this.activeCategory] = {
        timestamp: Date.now()
      }
    },
    // 导入数据
    handleImport() {
      this.$refs.industryFileInput.click()
    },
    async onIndustryFileChange(event) {
      const files = event.target.files
      if (!files || files.length === 0) return

      const file = files[0]
      if (!file.name.endsWith('.xlsx') && !file.name.endsWith('.xls')) {
        this.$message.error('只支持Excel文件格式（.xlsx或.xls）')
        return
      }

      this.importLoading = true
      try {
        // 确保有 evaluationId（草稿先保存一次）
        let evalId = this.evaluationId
        if (!evalId) {
          evalId = await this.saveEvaluationRecord()
        }
        const response = await uploadIndustryDevelopmentData(file, evalId)
        const result = response.data

        if (response.code === 200) {
          const rawList = result.dataList || []
          this.enterpriseList = rawList.map(item => ({
            parkName: item.parkName || item.belongParkName || '',
            enterpriseName: item.enterpriseName || '',
            creditCode: item.unifiedCreditCode || '',
            settledTime: item.settledDate || '',
            enterpriseAddress: item.registeredAddress || ''
          }))
          if (result.errorList && result.errorList.length > 0) {
            this.importErrors = result.errorList
            this.importErrorsVisible = true
          }
          this.$message.success(`导入成功！共${result.totalCount}条数据，成功${result.successCount}条，失败${result.errorCount}条`)
        } else {
          this.importErrors = result.errorList || []
          this.importErrorsVisible = true
          this.$message.error('导入失败，部分数据校验未通过')
        }
      } catch (error) {
        this.$message.error('导入失败：' + (error.message || '请稍后重试'))
      } finally {
        this.importLoading = false
        event.target.value = ''
      }
    },
    closeImportErrors() {
      this.importErrorsVisible = false
    },
    // 下载模板
    handleDownloadTemplate() {
      downloadTemplate('industry_development').then(res => {
        const blob = res.data || res
        const url = window.URL.createObjectURL(blob)
        const link = document.createElement('a')
        link.href = url
        link.download = '产业发展数据模板.xlsx'
        document.body.appendChild(link)
        link.click()
        document.body.removeChild(link)
        window.URL.revokeObjectURL(url)
      }).catch(err => {
        this.$message.error('下载失败：' + (err.message || '请稍后重试'))
      })
    },
    // 批量上传附件（基础指标）
    handleBatchUpload() {
      this.$refs.baseFileInput.click()
    },
    // 基础指标 - 文件选择回调
    async onBaseFileChange(event) {
      const files = event.target.files
      if (!files || files.length === 0) return

      for (let i = 0; i < files.length; i++) {
        try {
          const formData = new FormData()
          formData.append('file', files[i])
          formData.append('bizType', 'basic')

          const res = await uploadFile(formData)
          const data = res.data || res

          this.fileSections.push({
            fileName: data.name,
            fileUrl: data.url,
            fileId: data.id
          })
        } catch (err) {
          this.$message.error('文件上传失败：' + files[i].name)
        }
      }
      event.target.value = ''
    },
    // 企业培育 - 批量上传
    handleCultivationBatchUpload() {
      this.$refs.cultivationFileInput.click()
    },
    // 企业培育 - 文件选择回调
    async onCultivationFileChange(event) {
      const files = event.target.files
      if (!files || files.length === 0) return

      for (let i = 0; i < files.length; i++) {
        try {
          const formData = new FormData()
          formData.append('file', files[i])
          formData.append('bizType', 'cultivation')

          const res = await uploadFile(formData)
          const data = res.data || res

          this.cultivationFiles.push({
            id: null,
            projectName: '',
            fileId: data.id,
            fileName: data.name,
            fileUrl: data.url
          })
        } catch (err) {
          this.$message.error('文件上传失败：' + files[i].name)
        }
      }
      event.target.value = ''
    },
    // 企业培育 - 删除项目（含删除文件）
    async handleDeleteCultivation(index) {
      try {
        await this.$confirm('确定删除此项目？', '提示', { type: 'warning' })
        const item = this.cultivationFiles[index]
        if (item.id) {
          await deleteCultivationRecord(item.id).catch(() => {})
        }
        if (item.fileId) {
          await deleteFile(item.fileId).catch(() => {})
        }
        this.cultivationFiles.splice(index, 1)
      } catch (err) {
        if (err !== 'cancel' && err?.message) {
          this.$message.error('删除失败：' + err.message)
        }
      }
    },
    // 企业培育 - 删除文件（同时删除项目）
    async handleDeleteCultivationFile(index) {
      try {
        await this.$confirm('确定删除文件？', '提示', { type: 'warning' })
        const item = this.cultivationFiles[index]
        if (item.id) {
          await deleteCultivationRecord(item.id).catch(() => {})
        }
        if (item.fileId) {
          await deleteFile(item.fileId).catch(() => {})
        }
        this.cultivationFiles.splice(index, 1)
      } catch (err) {
        if (err !== 'cancel' && err?.message) {
          this.$message.error('删除失败：' + err.message)
        }
      }
    },
    // 科技创新 - 批量上传
    handleTechBatchUpload() {
      // 触发隐藏的文件输入框
      this.$refs.techFileInput.click()
    },
    // 科技创新 - 文件选择回调
    async onTechFileChange(event) {
      const files = event.target.files
      if (!files || files.length === 0) return
      console.log('files selected:', files)

      for (let i = 0; i < files.length; i++) {
        try {
          const formData = new FormData()
          formData.append('file', files[i])
          formData.append('bizType', 'tech_innovation')

          const res = await uploadFile(formData)
          console.log('upload result:', res)
          const fileData = res.data || res
          console.log('file data:', fileData)

          this.techInnovations.push({
            id: null,
            projectName: '',
            fileId: fileData.id,
            fileName: fileData.name,
            fileUrl: fileData.url,
            category: '',
            name: '',
            date: '',
            company: ''
          })
        } catch (err) {
          this.$message.error('文件上传失败：' + files[i].name)
        }
      }
      // 清空input，允许重复选择同一文件
      event.target.value = ''
    },
    // 科技创新 - 删除项目（含删除文件）
    async handleDeleteTech(index) {
      try {
        await this.$confirm('确定删除此项目？', '提示', { type: 'warning' })
        const item = this.techInnovations[index]
        if (item.id) {
          await deleteTechInnovation(item.id)
        }
        if (item.fileId) {
          await deleteFile(item.fileId).catch(() => {})
        }
        this.techInnovations.splice(index, 1)
      } catch (err) {
        // 用户取消或删除失败
        if (err !== 'cancel' && err?.message) {
          this.$message.error('删除失败：' + err.message)
        }
      }
    },
    // 科技创新 - 删除文件（同时删除项目）
    async handleDeleteTechFile(index) {
      try {
        await this.$confirm('确定删除文件？', '提示', { type: 'warning' })
        const item = this.techInnovations[index]
        if (item.id) {
          await deleteTechInnovation(item.id)
        }
        if (item.fileId) {
          await deleteFile(item.fileId).catch(() => {})
        }
        this.techInnovations.splice(index, 1)
      } catch (err) {
        if (err !== 'cancel' && err?.message) {
          this.$message.error('删除失败：' + err.message)
        }
      }
    },
    // 院所合作 - 批量上传
    handleProjectBatchUpload() {
      this.$refs.projectFileInput.click()
    },
    // 院所合作 - 文件选择回调
    async onProjectFileChange(event) {
      const files = event.target.files
      if (!files || files.length === 0) return

      for (let i = 0; i < files.length; i++) {
        try {
          const formData = new FormData()
          formData.append('file', files[i])
          formData.append('bizType', 'tech_project')

          const res = await uploadFile(formData)
          const data = res.data || res

          this.projects.push({
            id: null,
            name: '',
            fileId: data.id,
            fileName: data.name,
            fileUrl: data.url
          })
        } catch (err) {
          this.$message.error('文件上传失败：' + files[i].name)
        }
      }
      event.target.value = ''
    },
    // 删除项目
    async handleDeleteProject(index) {
      try {
        await this.$confirm('确定删除此项目？', '提示', { type: 'warning' })
        const item = this.projects[index]
        if (item.id) {
          await deleteTechProject(item.id)
        }
        if (item.fileId) {
          await deleteFile(item.fileId).catch(() => {})
        }
        this.projects.splice(index, 1)
      } catch (err) {
        if (err !== 'cancel' && err?.message) {
          this.$message.error('删除失败：' + err.message)
        }
      }
    },
    // 删除项目文件
    async handleDeleteProjectFile(index) {
      try {
        await this.$confirm('确定删除文件？', '提示', { type: 'warning' })
        const item = this.projects[index]
        if (item.id) {
          await deleteTechProject(item.id)
        }
        if (item.fileId) {
          await deleteFile(item.fileId).catch(() => {})
        }
        this.projects.splice(index, 1)
      } catch (err) {
        if (err !== 'cancel' && err?.message) {
          this.$message.error('删除失败：' + err.message)
        }
      }
    },
    // 服务能力 - 上传附件
    handleServiceUpload(type) {
      const inputRefs = {
        enterpriseService: 'serviceEnterpriseInput',
        oneStopService: 'serviceOneStopInput',
        unionActivity: 'serviceUnionInput',
        parkBrain: 'serviceParkBrainInput',
        inclusiveService: 'serviceInclusiveInput',
        personalizedService: 'servicePersonalizedInput',
        cooperationProject: 'serviceCooperationInput'
      }
      this.$refs[inputRefs[type]].click()
    },
    // 服务能力 - 文件选择回调
    async onServiceFileChange(event, type) {
      const files = event.target.files
      if (!files || files.length === 0) return

      for (let i = 0; i < files.length; i++) {
        try {
          const formData = new FormData()
          formData.append('file', files[i])
          formData.append('bizType', 'service_capacity')

          const res = await uploadFile(formData)
          const data = res.data || res

          this.serviceFiles[type].push({
            fileId: data.id,
            fileName: data.name,
            fileUrl: data.url
          })
        } catch (err) {
          this.$message.error('文件上传失败：' + files[i].name)
        }
      }
      event.target.value = ''
    },
    // 服务能力 - 删除文件
    async handleDeleteServiceFile(type, index) {
      try {
        await this.$confirm('确定删除文件？', '提示', { type: 'warning' })
        const file = this.serviceFiles[type][index]
        if (file.fileId) {
          await deleteFile(file.fileId).catch(() => {})
        }
        this.serviceFiles[type].splice(index, 1)
      } catch (err) {
        if (err !== 'cancel' && err?.message) {
          this.$message.error('删除失败：' + err.message)
        }
      }
    },
    // 效益产出 - 上传附件
    handleBenefitUpload() {
      this.$refs.benefitFileInput.click()
    },
    // 效益产出 - 文件选择回调
    async onBenefitFileChange(event) {
      const files = event.target.files
      if (!files || files.length === 0) return
      for (let i = 0; i < files.length; i++) {
        try {
          const formData = new FormData()
          formData.append('file', files[i])
          formData.append('bizType', 'benefit_output')
          const res = await uploadFile(formData)
          const data = res.data || res
          this.benefitFiles.push({
            fileId: data.id,
            fileName: data.name,
            fileUrl: data.url
          })
        } catch (err) {
          this.$message.error('文件上传失败：' + files[i].name)
        }
      }
      event.target.value = ''
    },
    // 效益产出 - 删除文件
    async handleDeleteBenefitFile(index) {
      try {
        await this.$confirm('确定删除文件？', '提示', { type: 'warning' })
        const file = this.benefitFiles[index]
        if (file.fileId) {
          await deleteFile(file.fileId).catch(() => {})
        }
        this.benefitFiles.splice(index, 1)
      } catch (err) {
        if (err !== 'cancel' && err?.message) {
          this.$message.error('删除失败：' + err.message)
        }
      }
    },
    // 预览文件
    handlePreview(item) {
      if (item && item.fileUrl) {
        // 传相对路径，FilePreview 组件通过 /api/common/download 接口获取（带鉴权）
        this.previewFileUrl = item.fileUrl
        this.previewFileName = item.fileName || item.name || '未知文件'
        this.previewVisible = true
      } else {
        this.$message.warning('文件地址不存在')
      }
    },
    // 其他 - 上传附件
    handleOtherUpload() {
      this.$refs.otherFileInput.click()
    },
    // 其他 - 文件选择回调
    async onOtherFileChange(event) {
      const files = event.target.files
      if (!files || files.length === 0) return
      for (let i = 0; i < files.length; i++) {
        try {
          const formData = new FormData()
          formData.append('file', files[i])
          formData.append('bizType', 'other')
          const res = await uploadFile(formData)
          const data = res.data || res
          this.otherFiles.push({
            fileId: data.id,
            fileName: data.name,
            fileUrl: data.url
          })
        } catch (err) {
          this.$message.error('文件上传失败：' + files[i].name)
        }
      }
      event.target.value = ''
    },
    // 其他 - 删除文件
    async handleDeleteOtherFile(index) {
      try {
        await this.$confirm('确定删除文件？', '提示', { type: 'warning' })
        const file = this.otherFiles[index]
        if (file.fileId) {
          await deleteFile(file.fileId).catch(() => {})
        }
        this.otherFiles.splice(index, 1)
      } catch (err) {
        if (err !== 'cancel' && err?.message) {
          this.$message.error('删除失败：' + err.message)
        }
      }
    },
    // 预览承诺函模板
    handlePreviewPromiseTemplate() {
      const templateName = '评价年度承诺函模板.docx'
      const templateUrl = '/api/files/preview/template/promise_letter_template.docx'
      this.previewFileUrl = templateUrl
      this.previewFileName = templateName
      this.previewVisible = true
    }
  }
}
</script>

<style scoped>
.evaluation-add {
  padding: 16px 20px 30px;
  min-height: 100%;
  box-sizing: border-box;
}

/* 顶部标题栏 */
.detail-header {
  display: flex;
  align-items: center;
  background: #fff;
  padding: 14px 20px;
  border-radius: 8px;
  box-shadow: 0 2px 12px 0 rgba(0, 0, 0, 0.06);
  margin-bottom: 16px;
}

.header-left .title {
  font-size: 16px;
  font-weight: 600;
  color: #303133;
}

/* 返回导航 */
.detail-nav {
  background: #fff;
  padding: 14px 20px 0;
  border-radius: 8px 8px 0 0;
  box-shadow: 0 2px 12px 0 rgba(0, 0, 0, 0.06);
}

.back-btn {
  display: inline-flex;
  align-items: center;
  gap: 6px;
  color: #606266;
  font-size: 14px;
  cursor: pointer;
  padding-bottom: 10px;
  border-bottom: 1px solid #ebeef5;
  transition: color 0.2s;
}

.back-btn:hover {
  color: #409eff;
}

.back-btn .el-icon-back {
  font-size: 16px;
}

/* 左右布局 */
.add-layout {
  display: flex;
  gap: 16px;
  min-height: calc(100vh - 180px);
}

/* 左侧分类列表 */
.category-sidebar {
  width: 200px;
  flex-shrink: 0;
  background: #fff;
  border-radius: 8px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.04);
  overflow: hidden;
}

.category-item {
  display: flex;
  align-items: center;
  padding: 18px 20px;
  cursor: pointer;
  transition: background-color 0.2s, color 0.2s;
  border-left: 3px solid transparent;
}

.category-item:hover {
  background-color: #F5F7FA;
}

.category-item--active {
  background-color: #ECF5FF;
  border-left-color: #409EFF;
}

.category-item--active .category-name {
  color: #409EFF;
  font-weight: 500;
}

.category-item--active .category-index {
  color: #409EFF;
}

/* 已完成流程标签样式 */
.category-item--completed {
  background-color: #F0F9EB;
  border-left-color: #67C23A;
}

.category-item--completed .category-name {
  color: #67C23A;
  font-weight: 500;
}

.category-item--completed .category-index {
  color: #67C23A;
}

.category-item--completed .category-check {
  color: #67C23A;
  font-weight: bold;
}

/* 既激活又完成的样式（当前正在编辑且已完成） */
.category-item--active.category-item--completed {
  background-color: #ECF5FF;
  border-left-color: #409EFF;
}

.category-item--active.category-item--completed .category-name {
  color: #409EFF;
}

.category-item--active.category-item--completed .category-index {
  color: #409EFF;
}

.category-item--active.category-item--completed .category-check {
  color: #67C23A;
}
.category-item--viewed {
  background-color: #F0F9EB;
  border-left-color: #67C23A;
}

.category-item--viewed .category-name {
  color: #67C23A;
}

.category-item--viewed .category-index {
  color: #67C23A;
}

/* 既激活又已读的样式（查看模式下当前正在浏览） */
.category-item--active.category-item--viewed {
  background-color: #ECF5FF;
  border-left-color: #409EFF;
}

.category-item--active.category-item--viewed .category-name,
.category-item--active.category-item--viewed .category-index {
  color: #409EFF;
}

/* 既激活又已读又完成的样式 */
.category-item--active.category-item--viewed.category-item--completed {
  background-color: #ECF5FF;
  border-left-color: #409EFF;
}

.category-item--active.category-item--viewed.category-item--completed .category-name,
.category-item--active.category-item--viewed.category-item--completed .category-index {
  color: #409EFF;
}

/* 只读模式样式 */
.category-item--readonly {
  cursor: default;
}

.category-item--readonly:hover {
  background-color: inherit;
}

.category-index {
  font-size: 14px;
  color: #909399;
  min-width: 28px;
}

.category-name {
  font-size: 14px;
  color: #303133;
  flex: 1;
}

.category-arrow {
  font-size: 12px;
  color: #C0C4CC;
  transition: color 0.2s, transform 0.2s;
}

.category-item:hover .category-arrow {
  color: #409EFF;
  transform: translateX(4px);
}

.category-item--active .category-arrow {
  color: #409EFF;
}

/* 右侧内容区 */
.category-content {
  flex: 1;
  background: #fff;
  border-radius: 8px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.04);
  display: flex;
  flex-direction: column;
  overflow: hidden;
}

.content-panel {
  display: flex;
  flex-direction: column;
  height: 100%;
}

.panel-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 18px 24px;
  border-bottom: 1px solid #EBEEF5;
  flex-shrink: 0;
}

.panel-title-bar {
  display: flex;
  align-items: center;
  gap: 10px;
}

.title-decoration {
  width: 4px;
  height: 18px;
  background: #409EFF;
  border-radius: 2px;
}

.panel-title {
  font-size: 16px;
  font-weight: 500;
  color: #303133;
}

.panel-body {
  flex: 1;
  padding: 24px;
}

.panel-footer {
  display: flex;
  justify-content: flex-end;
  align-items: center;
  gap: 12px;
  padding: 18px 24px;
  border-top: 1px solid #EBEEF5;
  flex-shrink: 0;
}

.panel-footer .el-button {
  border-radius: 4px;
}

/* 指标项 */
.indicator-item {
  display: flex;
  align-items: flex-start;
  gap: 8px;
  margin-bottom: 24px;
  padding: 16px;
  background: #FAFAFA;
  border-radius: 6px;
}

.indicator-number {
  font-size: 14px;
  color: #409EFF;
  font-weight: 500;
  flex-shrink: 0;
  line-height: 1.6;
}

.indicator-desc {
  font-size: 14px;
  color: #606266;
  line-height: 1.6;
}

/* 单选框 */
.acknowledge-radio {
  margin-left: 16px;
}

.acknowledge-radio >>> .el-radio__label {
  font-size: 14px;
  color: #606266;
}

/* 占位文字 */
.placeholder-text {
  font-size: 14px;
  color: #909399;
  text-align: center;
  padding: 80px 0;
}

/* 产业发展 - 指标说明列表 */
.indicator-list {
  margin-bottom: 20px;
  padding: 12px 16px;
  background: #FAFAFA;
  border-radius: 6px;
}

.indicator-text {
  font-size: 14px;
  color: #606266;
  line-height: 1.8;
}

.indicator-text-bold {
  font-weight: 600;
  color: #303133;
}

/* 操作按钮栏 */
.action-bar {
  margin-bottom: 16px;
}

.action-bar .el-button {
  border-radius: 20px !important;
}

/* 产业发展表格 */
.enterprise-table {
  font-size: 13px;
}

.enterprise-table >>> .el-table__header th {
  background-color: #F5F7FA !important;
  color: #303133;
  font-weight: 500;
}

/* 企业培育 - 批量上传按钮栏 */
.batch-upload-bar {
  margin: 20px 0 16px;
}

.batch-upload-bar .el-button {
  border-radius: 20px !important;
}

/* 企业培育 - 文件列表 */
.file-list-wrapper {
  display: flex;
  flex-direction: column;
  gap: 12px;
}

.file-section {
  background: #fff;
  border: 1px solid #EBEEF5;
  border-radius: 6px;
  padding: 12px 16px;
}

.section-title-row {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 8px;
  padding-bottom: 8px;
  border-bottom: 1px solid #EBEEF5;
}

.section-title {
  font-size: 14px;
  font-weight: 500;
  color: #303133;
}

.file-row {
  display: flex;
  align-items: center;
  gap: 8px;
  padding: 6px 0;
}

.file-row .el-icon-document {
  color: #409EFF;
  font-size: 16px;
  flex-shrink: 0;
}

.file-name-input {
  flex: 1;
}

.file-name-input >>> .el-input__inner {
  border: none;
  background: transparent;
  padding: 0 4px;
  font-size: 13px;
}

.file-name-input >>> .el-input__inner:focus {
  border: 1px solid #409EFF;
  background: #fff;
}

.action-link {
  font-size: 13px;
  text-decoration: none;
  cursor: pointer;
  flex-shrink: 0;
}

.preview-link {
  color: #409EFF;
}

.delete-link {
  color: #F56C6C;
}

/* 科技创新 - 列表容器 */
.tech-list-wrapper {
  display: flex;
  flex-direction: column;
  gap: 12px;
}

.tech-item {
  background: #fff;
  border: 1px solid #EBEEF5;
  border-radius: 6px;
  padding: 12px 16px;
}

.tech-header {
  display: flex;
  align-items: center;
  gap: 8px;
  margin-bottom: 10px;
}

.tech-label {
  font-size: 14px;
  font-weight: 500;
  color: #303133;
  white-space: nowrap;
}

.tech-project-input {
  flex: 1;
  max-width: 200px;
}

.tech-project-input >>> .el-input__inner {
  border: 1px solid transparent;
  background: transparent;
  font-size: 14px;
  font-weight: 500;
  color: #303133;
}

.tech-project-input >>> .el-input__inner:focus {
  border-color: #409EFF;
  background: #fff;
}

.tech-file-area {
  background: #F5F7FA;
  border-radius: 4px;
  padding: 8px 12px;
  margin-bottom: 10px;
}

.tech-file-row {
  display: flex;
  align-items: center;
  gap: 8px;
  font-size: 13px;
  color: #606266;
}

.tech-file-row .el-icon-document {
  color: #409EFF;
  font-size: 16px;
}

.tech-file-name {
  flex: 1;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}

.tech-info-row {
  display: flex;
  align-items: center;
  gap: 8px;
}

.tech-cell {
  flex: 1;
}

.category-cell {
  max-width: 120px;
}

/* 院所合作 - 项目列表 */
.project-list-wrapper {
  display: flex;
  flex-direction: column;
  gap: 12px;
}

.project-item {
  background: #fff;
  border: 1px solid #EBEEF5;
  border-radius: 6px;
  padding: 12px 16px;
}

.project-header {
  display: flex;
  align-items: center;
  gap: 8px;
  margin-bottom: 10px;
}

.project-name-input {
  flex: 1;
  max-width: 200px;
}

.project-name-input >>> .el-input__inner {
  border: 1px solid transparent;
  background: transparent;
  font-size: 14px;
  font-weight: 500;
  color: #303133;
}

.project-name-input >>> .el-input__inner:focus {
  border-color: #409EFF;
  background: #fff;
}

.edit-icon {
  color: #409EFF;
  cursor: pointer;
  font-size: 16px;
}

.project-file-row {
  display: flex;
  align-items: center;
  gap: 8px;
  font-size: 13px;
  color: #606266;
}

.project-file-row .el-icon-document {
  color: #409EFF;
  font-size: 16px;
}

.project-file-row .file-name {
  flex: 1;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}

/* 文件预览弹窗 */
.simple-preview {
  height: calc(80vh - 120px);
}

.preview-iframe {
  width: 100%;
  height: 100%;
}

/* 服务能力样式 */
.service-row {
  display: flex;
  gap: 24px;
  margin-bottom: 16px;
}

.service-col {
  flex: 1;
  background: #F5F7FA;
  border-radius: 6px;
  padding: 16px;
}

.service-col-full {
  width: 100%;
  background: #F5F7FA;
  border-radius: 6px;
  padding: 16px;
}

.service-title {
  font-size: 14px;
  font-weight: 500;
  color: #303133;
  margin-bottom: 12px;
}

.service-hint {
  font-size: 12px;
  color: #909399;
  margin-bottom: 8px;
}

.service-upload-btn {
  margin-bottom: 12px;
  border-radius: 20px !important;
}

.service-file-list {
  display: flex;
  flex-direction: column;
  gap: 8px;
}

.service-file-item {
  display: flex;
  align-items: center;
  gap: 8px;
  font-size: 13px;
  color: #606266;
  padding: 8px 12px;
  background: #fff;
  border-radius: 4px;
}

.service-file-item .el-icon-document {
  color: #409EFF;
  font-size: 16px;
  flex-shrink: 0;
}

.service-file-name {
  flex: 1;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}

/* 导入错误列表 */
.import-error-list {
  max-height: 400px;
  overflow-y: auto;
}

.import-error-item {
  display: flex;
  flex-wrap: wrap;
  align-items: flex-start;
  gap: 8px;
  padding: 10px 0;
  border-bottom: 1px solid #EBEEF5;
}

.import-error-item:last-child {
  border-bottom: none;
}

.error-row {
  background: #fef0f0;
  color: #f56c6c;
  padding: 2px 8px;
  border-radius: 4px;
  font-size: 12px;
  font-weight: 600;
}

.error-enterprise {
  flex: 1;
  font-size: 13px;
  color: #303133;
  min-width: 150px;
}

.error-message {
  color: #f56c6c;
  font-size: 13px;
}

.promise-template {
  background: #F5F7FA;
  border-radius: 6px;
  padding: 16px;
  margin-top: 16px;
}

.promise-template-header {
  display: flex;
  align-items: center;
  gap: 10px;
  font-size: 14px;
  color: #303133;
}

.promise-template-header .el-icon-document {
  color: #409EFF;
  font-size: 18px;
}

.promise-template-name {
  flex: 1;
}
</style>
