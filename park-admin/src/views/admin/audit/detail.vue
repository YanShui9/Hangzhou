<template>
  <div class="audit-detail-container">
    <div class="breadcrumb-bar">
      <span class="breadcrumb-link" @click="goBackToList">评价审核</span>
      <span class="breadcrumb-sep">/</span>
      <span class="breadcrumb-current">审核详情</span>
    </div>

    <div class="detail-layout">
      <div class="side-nav">
        <el-menu
          :default-active="activeSection"
          class="nav-menu"
          :class="{ 'is-locked': isAuditMode && !allSectionsVisited }"
          @select="handleMenuSelect"
        >
          <el-menu-item
            v-for="item in navItems"
            :key="item.index"
            :index="item.index"
            :class="{ 'is-visited': visitedSections[item.index] }"
          >{{ item.label }}</el-menu-item>
        </el-menu>
      </div>

      <div class="main-content">
        <div class="content-header">
          <div class="section-title">
            <span class="title-icon">|</span>
            <span class="title-text">{{ sectionTitle }}</span>
          </div>
          <div class="header-actions">
            <el-button
              v-if="isAuditMode"
              type="primary"
              size="small"
              :loading="saveLoading"
              @click="handleSaveDraft"
            >保存</el-button>
            <div class="audit-record-btn" @click="toggleAuditRecord">
              <i class="el-icon-document"></i>
              <span>审核记录</span>
            </div>
          </div>
        </div>

        <div class="content-body">
          <!-- ==================== 1-基础指标 ==================== -->
          <div v-if="activeSection === 'basic'" class="section-content" v-loading="detailLoading">
            <div class="rule-box">
              <p class="rule-text">
                <span class="rule-num">①</span>
                评价年度内参评园区需符合《杭州市升级版小微企业园建设和管理工作指引(试行)》明确的小微企业园认定条件，不具备的直接列D档。
              </p>
            </div>
            <div class="radio-group">
              <label class="radio-label">
                <input type="radio" name="basicResult1" v-model="formData.basicResult1" value="通过" :disabled="true" />
                <span>通过</span>
              </label>
              <label class="radio-label">
                <input type="radio" name="basicResult1" v-model="formData.basicResult1" value="驳回" :disabled="true" />
                <span>驳回</span>
              </label>
              <label class="radio-label">
                <input type="radio" name="basicResult1" v-model="formData.basicResult1" value="暂缓" :disabled="true" />
                <span>暂缓</span>
              </label>
              <label class="radio-label">
                <input type="radio" name="basicResult1" v-model="formData.basicResult1" value="退出" :disabled="true" />
                <span>退出</span>
              </label>
            </div>
            <div class="file-section">
              <div class="section-subtitle">高新技术企业名单</div>
              <div v-if="!highTechFileList || highTechFileList.length === 0" class="file-empty">暂无附件</div>
              <div v-for="(file, idx) in highTechFileList" :key="idx" class="file-item-row">
                <span class="file-icon"><i class="el-icon-document"></i></span>
                <span class="file-name" :title="file.name">{{ file.name }}</span>
                <el-link
                  v-if="file.url"
                  type="primary"
                  :href="file.url"
                  target="_blank"
                  :underline="false"
                  class="file-preview"
                >预览</el-link>
                <span v-else class="file-preview-disabled">预览</span>
              </div>
            </div>
          </div>

          <!-- ==================== 2-产业发展 ==================== -->
          <div v-else-if="activeSection === 'industry'" class="section-content" v-loading="detailLoading">
            <div class="rule-box">
              <p class="rule-text">
                <span class="rule-num">①</span>
                园区产业围绕五大产业生态圈（智能物联、生物医药、高端装备、新材料和绿色能源五大产业生态圈）集聚发展，并以此形成主导产业的，得5分。
              </p>
              <p class="rule-text">
                <span class="rule-num">②</span>
                园区主导产业明确，效益突出，评价年度内主导产业产值（营收）及入驻企业数占比均超过50%，两项占比在50%的基础上每增加10%，得10分。
              </p>
              <p class="rule-text">
                <span class="rule-num">③</span>
                评价年度内园区生产制造企业不少于10家，产值占比不低于60%，产值占比在60%的基础上每增加10%，得10分。
              </p>
              <p class="rule-text">
                <span class="rule-num">④</span>
                评价年度内园区注册企业列表名单。
              </p>
            </div>

            <div class="data-empty" v-if="!industryTableData || industryTableData.length === 0">
              暂无数据
            </div>
            <el-table
              v-else
              :data="industryTableData"
              border
              stripe
              style="width: 100%"
              class="audit-table"
            >
              <el-table-column type="index" label="序号" width="80" align="center" />
              <el-table-column prop="parkName" label="园区名称" min-width="160" show-overflow-tooltip />
              <el-table-column prop="enterpriseName" label="入驻企业名称" min-width="220" show-overflow-tooltip />
              <el-table-column prop="creditCode" label="统一社会信用代码" width="200" show-overflow-tooltip />
              <el-table-column prop="entryTime" label="入驻起止时间" width="150" align="center" show-overflow-tooltip />
              <el-table-column prop="address" label="企业注册地址" min-width="260" show-overflow-tooltip />
            </el-table>
          </div>

          <!-- ==================== 3-企业培育 ==================== -->
          <div v-else-if="activeSection === 'enterprise'" class="section-content">
            <div class="rule-box">
              <p class="rule-text">
                <span class="rule-num">①</span>
                园区内存量的规模以上工业企业每家得1分；评价年度内园区新新增1家规模以上工业企业得2分；评价年度内有规模以上工业企业退到规下的，每退减1家减1分。
              </p>
              <p class="rule-text">
                <span class="rule-num">②</span>
                评价年度内园区内企业每新增一家国家级制造业单项冠军得4分；每新增一家上市企业得3分；每新增一家国家级专精特新小巨人或省级隐形冠军企业得2分；每新增一家省专精特新中小企业或国家高新技术企业得1分；每新增一家创新型中小企业得0.5分。同一企业不重复计算。
              </p>
              <p class="rule-text">
                <span class="rule-num">③</span>
                园区建立专属产业基金，评价年度内园区内企业"投早投小投创新"案例，每新增1个得1分。
              </p>
            </div>

            <div class="file-section">
              <div class="section-subtitle">2025年高新技术企业名单</div>
              <div class="file-item-row">
                <span class="file-icon"><i class="el-icon-document"></i></span>
                <span class="file-name">2025年高新技术企业名单.pdf</span>
                <span class="file-preview">预览</span>
                <div class="score-input-inline">
                  <el-input
                    v-model="formData.enterpriseScore"
                    placeholder="请输入得分（满分5分）"
                    size="small"
                    style="width: 180px;"
                    :disabled="!isAuditMode"
                  />
                </div>
              </div>
            </div>

            <div class="opinion-section">
              <div class="section-subtitle">意见</div>
              <el-input
                v-model="formData.enterpriseOpinion"
                type="textarea"
                :rows="4"
                placeholder="请输入您的意见"
                maxlength="500"
                show-word-limit
                class="opinion-textarea"
                :disabled="!isAuditMode"
              />
            </div>
          </div>

          <!-- ==================== 4-科技创新 ==================== -->
          <div v-else-if="activeSection === 'tech'" class="section-content" v-loading="detailLoading">
            <!-- 第一区块：高层次人才 -->
            <div class="rule-box">
              <p class="rule-text">
                <span class="rule-num">①</span>
                评价年度内园区内企业新增省级及以上首台(套)装备、首版次、首批次、省级优秀工业新产品、浙江制造精品的，每项得2分。
              </p>
              <p class="rule-text">
                <span class="rule-num">②</span>
                评价年度内园区新增国家级、省级、市级企业研发机构的，每项分别得3分、2分、1分。同一企业(机构)不重复计算。
              </p>
              <p class="rule-text">
                <span class="rule-num">③</span>
                评价年度内园区新增国家级、省级、市级以上科研创新、企业孵化及检验检测服务等公共服务平台的，每项得3分检测服务、2分等公共服务平台的，每项得2分。同一项目不重复计算。
              </p>
              <p class="rule-text">
                <span class="rule-num">④</span>
                评价年度内园区新引进独立注册的市级及以上科研创新、企业、孵化及检验检测服务等公共服务平台的，每项得2分。同一项目不重复计算。
              </p>
              <p class="rule-text">
                <span class="rule-num">⑤</span>
                根据杭州市高层次人才分类目录标准，评价年度内园区企业新认定A、B、C、D类人才的，每人分别得3分、2分、1.5分、1分。
              </p>
            </div>

            <!-- 人才表行 -->
            <div class="data-empty" v-if="!techTableData || techTableData.length === 0">暂无人才数据</div>
            <div class="tech-rows" v-else>
              <div v-for="(item, index) in techTableData" :key="index" class="tech-row">
                <div class="tech-row-cell"><span class="field-label-inline">人才等级</span><span class="field-value-inline">{{ item.level || '-' }}</span></div>
                <div class="tech-row-cell"><span class="field-label-inline">姓名</span><span class="field-value-inline">{{ item.name || '-' }}</span></div>
                <div class="tech-row-cell"><span class="field-label-inline">日期</span><span class="field-value-inline">{{ item.date || '-' }}</span></div>
                <div class="tech-row-cell"><span class="field-label-inline">所属企业</span><span class="field-value-inline">{{ item.company || '-' }}</span></div>
                <div class="tech-row-cell tech-score-wrap">
                  <span class="field-label-inline">得分</span>
                  <el-input v-model="item.score" size="small" style="width: 140px" placeholder="请输入得分" :disabled="true" />
                </div>
              </div>
            </div>

            <!-- 意见 -->
            <div class="opinion-section">
              <div class="section-subtitle">意见</div>
              <el-input
                v-model="formData.techOpinion"
                type="textarea"
                :rows="4"
                placeholder="请输入您的意见"
                maxlength="500"
                show-word-limit
                class="opinion-textarea"
                :disabled="!isAuditMode"
              />
            </div>

            <!-- 第二区块：科研合作 -->
            <div class="rule-box" style="margin-top: 20px;">
              <p class="rule-text">
                <span class="rule-num">⑤</span>
                园区与科研院所建立合作关系，在园区开展科研成果转移转化并在评价年度形成500万元以上产出的，每项得1分。
              </p>
            </div>

            <div class="file-section">
              <div class="section-subtitle">相关附件</div>
              <div v-if="!techFileList || techFileList.length === 0" class="file-empty">暂无附件</div>
              <div v-for="(file, idx) in techFileList" :key="'t' + idx" class="file-item-row">
                <span class="file-icon"><i class="el-icon-document"></i></span>
                <span class="file-name" :title="file.name">{{ file.name }}</span>
                <el-link
                  v-if="file.url"
                  type="primary"
                  :href="file.url"
                  target="_blank"
                  :underline="false"
                  class="file-preview"
                >预览</el-link>
                <span v-else class="file-preview-disabled">预览</span>
                <div class="score-input-inline">
                  <el-input
                    v-model="formData.techScore2"
                    placeholder="请输入得分（满分5分）"
                    size="small"
                    style="width: 180px;"
                    :disabled="!isAuditMode"
                  />
                </div>
              </div>
            </div>

            <div class="opinion-section">
              <div class="section-subtitle">意见</div>
              <el-input
                v-model="formData.techOpinion2"
                type="textarea"
                :rows="4"
                placeholder="请输入您的意见"
                maxlength="500"
                show-word-limit
                class="opinion-textarea"
                :disabled="!isAuditMode"
              />
            </div>
          </div>

          <!-- ==================== 5-服务能力 ==================== -->
          <div v-else-if="activeSection === 'service'" class="section-content">
            <!-- 第一区块 -->
            <div class="rule-box">
              <p class="rule-text">
                <span class="rule-num">①</span>
                园区建立助企服务站(工作室)并有明确完善助企服务机制的，得5分；对入园企业项目报批实行一站式全程代办服务的，得5分；按规定组织企业开展党员、团员及工会活动的，得5分。
              </p>
            </div>

            <!-- 双栏文件区 -->
            <div class="two-col-section">
              <div class="col-item">
                <div class="section-subtitle">助企服务站建设材料</div>
                <div class="file-item-row">
                  <span class="file-icon"><i class="el-icon-document"></i></span>
                  <span class="file-name">助企服务站建设材料.pdf</span>
                  <span class="file-preview">预览</span>
                  <div class="score-input-inline">
                    <el-input v-model="formData.serviceScore1" placeholder="请输入得分（满分5分）" size="small" style="width: 180px;" :disabled="!isAuditMode" />
                  </div>
                </div>
              </div>
              <div class="col-item">
                <div class="section-subtitle">一站式代办服务材料</div>
                <div class="file-item-row">
                  <span class="file-icon"><i class="el-icon-document"></i></span>
                  <span class="file-name">助企服务站建设材料.pdf</span>
                  <span class="file-preview">预览</span>
                  <div class="score-input-inline">
                    <el-input v-model="formData.serviceScore2" placeholder="请输入得分（满分5分）" size="small" style="width: 180px;" :disabled="!isAuditMode" />
                  </div>
                </div>
              </div>
            </div>

            <!-- 党团工会 -->
            <div class="file-section" style="margin-top: 12px;">
              <div class="section-subtitle">党团工会活动材料</div>
              <div class="file-item-row">
                <span class="file-icon"><i class="el-icon-document"></i></span>
                <span class="file-name">助企服务站建设材料.pdf</span>
                <span class="file-preview">预览</span>
                <div class="score-input-inline">
                  <el-input v-model="formData.serviceScore3" placeholder="请输入得分（满分5分）" size="small" style="width: 180px;" :disabled="!isAuditMode" />
                </div>
              </div>
            </div>

            <div class="opinion-section">
              <div class="section-subtitle">意见</div>
              <el-input
                v-model="formData.serviceOpinion1"
                type="textarea"
                :rows="4"
                placeholder="请输入您的意见"
                maxlength="500"
                show-word-limit
                class="opinion-textarea"
                :disabled="!isAuditMode"
              />
            </div>

            <!-- 第二区块：园区大脑 -->
            <div class="rule-box" style="margin-top: 20px;">
              <p class="rule-text">
                <span class="rule-num">②</span>
                建立园区大脑，并获得省级数字化示范园区的，得5分，未获得省级数字化示范园区，但能集聚园区数据资源，集成相关功能模块，利用实时数据优化园区公共资源，实现园区治理智慧化，助力园区运营升级的，得4分；未建立园区大脑的最高得3分，其中，有信息发布平台、数字化建设等独立系统的，可得1分；已实施数字化物业管理的，可得1分；具有数字化管理专门机构的，可得1分；实现园区安全生产数字化监管的，可得1分。
              </p>
            </div>

            <div class="file-section">
              <div class="section-subtitle">园区大脑数字化相关资料</div>
              <div class="file-item-row">
                <span class="file-icon"><i class="el-icon-document"></i></span>
                <span class="file-name">助企服务站建设材料.pdf</span>
                <span class="file-preview">预览</span>
                <div class="score-input-inline">
                  <el-input v-model="formData.serviceScore4" placeholder="请输入得分（满分5分）" size="small" style="width: 180px;" :disabled="!isAuditMode" />
                </div>
              </div>
            </div>

            <div class="opinion-section">
              <div class="section-subtitle">意见</div>
              <el-input
                v-model="formData.serviceOpinion2"
                type="textarea"
                :rows="4"
                placeholder="请输入您的意见"
                maxlength="500"
                show-word-limit
                class="opinion-textarea"
                :disabled="!isAuditMode"
              />
            </div>

            <!-- 第三区块：普惠服务 -->
            <div class="rule-box" style="margin-top: 20px;">
              <p class="rule-text">
                <span class="rule-num">③</span>
                评价年度内开展普惠性服务活动10场以上(每场活动参加企业5家以上),得3分；开展针对性个性化助企服务活动20次以上的，得3分。
              </p>
            </div>

            <div class="two-col-section">
              <div class="col-item">
                <div class="section-subtitle">普惠性服务活动</div>
                <div class="file-item-row">
                  <span class="file-icon"><i class="el-icon-document"></i></span>
                  <span class="file-name">助企服务站建设材料.pdf</span>
                  <span class="file-preview">预览</span>
                  <div class="score-input-inline">
                    <el-input v-model="formData.serviceScore5" placeholder="请输入得分（满分3分）" size="small" style="width: 180px;" :disabled="!isAuditMode" />
                  </div>
                </div>
              </div>
              <div class="col-item">
                <div class="section-subtitle">个性化服务活动</div>
                <div class="file-item-row">
                  <span class="file-icon"><i class="el-icon-document"></i></span>
                  <span class="file-name">助企服务站建设材料.pdf</span>
                  <span class="file-preview">预览</span>
                  <div class="score-input-inline">
                    <el-input v-model="formData.serviceScore6" placeholder="请输入得分（满分3分）" size="small" style="width: 180px;" :disabled="!isAuditMode" />
                  </div>
                </div>
              </div>
            </div>

            <div class="opinion-section">
              <div class="section-subtitle">意见</div>
              <el-input
                v-model="formData.serviceOpinion3"
                type="textarea"
                :rows="4"
                placeholder="请输入您的意见"
                maxlength="500"
                show-word-limit
                class="opinion-textarea"
                :disabled="!isAuditMode"
              />
            </div>

            <!-- 第四区块：合作项目 -->
            <div class="rule-box" style="margin-top: 20px;">
              <p class="rule-text">
                <span class="rule-num">④</span>
                园区与杭州市范围内的其他小微园形成合作关系，进行管理服务输出，评价年度内取得产业、科创合作3个以上项目的，得3分。
              </p>
            </div>

            <div class="file-section">
              <div class="section-subtitle">项目名称</div>
              <div class="file-item-row">
                <span class="file-icon"><i class="el-icon-document"></i></span>
                <span class="file-name">助企服务站建设材料.pdf</span>
                <span class="file-preview">预览</span>
                <div class="score-input-inline">
                  <el-input v-model="formData.serviceScore7" placeholder="请输入得分（满分3分）" size="small" style="width: 180px;" :disabled="!isAuditMode" />
                </div>
              </div>
            </div>

            <div class="opinion-section">
              <div class="section-subtitle">意见</div>
              <el-input
                v-model="formData.serviceOpinion4"
                type="textarea"
                :rows="4"
                placeholder="请输入您的意见"
                maxlength="500"
                show-word-limit
                class="opinion-textarea"
                :disabled="!isAuditMode"
              />
            </div>
          </div>

          <!-- ==================== 6-效益产出 ==================== -->
          <div v-else-if="activeSection === 'benefit'" class="section-content">
            <div class="rule-box">
              <p class="rule-text">
                <span class="rule-num">①</span>
                评价年度亩均税收达到全市制造园区平均水平：1.5-2倍得2分；2-2.5倍得4分；2.5-3倍得6分；3-3.5倍得8分；3.5倍及以上得10分。
              </p>
              <p class="rule-text">
                <span class="rule-num">②</span>
                评价年度亩均产出达到全市制造园区平均水平：1.5-2倍得2分；2-2.5倍得4分；2.5-3倍得6分；3-3.5倍得8分；3.5倍及以上得10分。
              </p>
              <p class="rule-text">
                <span class="rule-num">③</span>
                贯彻落实集约发展理念，通过改造提升实现工业上楼、效益提升的，得5分。
              </p>
            </div>

            <div class="file-section">
              <div class="section-subtitle">项目名称</div>
              <div class="file-item-row">
                <span class="file-icon"><i class="el-icon-document"></i></span>
                <span class="file-name">2025年高新技术企业名单.pdf</span>
                <span class="file-preview">预览</span>
                <div class="score-input-inline">
                  <el-input
                    v-model="formData.benefitScore"
                    placeholder="请输入得分"
                    size="small"
                    style="width: 180px;"
                    :disabled="!isAuditMode"
                  />
                </div>
              </div>
            </div>

            <div class="opinion-section">
              <div class="section-subtitle">意见</div>
              <el-input
                v-model="formData.benefitOpinion"
                type="textarea"
                :rows="4"
                placeholder="请输入您的意见"
                maxlength="500"
                show-word-limit
                class="opinion-textarea"
                :disabled="!isAuditMode"
              />
            </div>
          </div>

          <!-- ==================== 7-安全生产 ==================== -->
          <div v-else-if="activeSection === 'safety'" class="section-content">
            <div class="rule-box">
              <p class="rule-text">
                <span class="rule-num">①</span>
                未落实《杭州市小微创业园安全管理通则》要求，经查实的，扣2分；
              </p>
            </div>
            <el-input v-model="formData.safetyScore1" placeholder="请输入得分" size="small" style="width: 300px; margin-bottom: 16px;" :disabled="!isAuditMode" />

            <div class="rule-box">
              <p class="rule-text">
                <span class="rule-num">②</span>
                未签订消防安全责任书的，扣2分；未落实培训、演练要求的，扣2分；
              </p>
            </div>
            <el-input v-model="formData.safetyScore2" placeholder="请输入得分" size="small" style="width: 300px; margin-bottom: 16px;" :disabled="!isAuditMode" />

            <div class="rule-box">
              <p class="rule-text">
                <span class="rule-num">③</span>
                消防设施器材不完整或过期的，扣2分；
              </p>
            </div>
            <el-input v-model="formData.safetyScore3" placeholder="请输入得分" size="small" style="width: 300px; margin-bottom: 16px;" :disabled="!isAuditMode" />

            <div class="rule-box">
              <p class="rule-text">
                <span class="rule-num">④</span>
                存在安全隐患被省、市主管部门通报的，每次扣2分。
              </p>
            </div>
            <el-input v-model="formData.safetyScore4" placeholder="请输入得分" size="small" style="width: 300px; margin-bottom: 16px;" :disabled="!isAuditMode" />

            <div class="rule-box">
              <p class="rule-text">
                <span class="rule-num">⑤</span>
                近一年内发生较大以上的安全生产事故或较大影响力的社会事件，园区安全生产隐患未按期整改的，直接列D档。
              </p>
            </div>
            <div class="radio-group">
              <label class="radio-label">
                <input type="radio" name="safetyDGrade" v-model="formData.safetyDGrade" value="yes" :disabled="!isAuditMode" />
                <span>列入D档</span>
              </label>
              <label class="radio-label">
                <input type="radio" name="safetyDGrade" v-model="formData.safetyDGrade" value="no" :disabled="!isAuditMode" />
                <span>不列入D档</span>
              </label>
            </div>
          </div>

          <!-- ==================== 8-其他 ==================== -->
          <div v-else-if="activeSection === 'other'" class="section-content">
            <div class="rule-box">
              <p class="rule-text">
                <span class="rule-num">①</span>
                园区在浙江省小微企业园信息管理系统中未按时完整报送小微企业园季度运行数据，少报一次减2分；年度数据未报的直接列D档。
              </p>
            </div>

            <div class="rule-box">
              <p class="rule-text">
                <span class="rule-num">②</span>
                申报材料、报送数据作假的，运营管理机构被列入杭州市公共信用信息平台失信联合惩戒对象名单的，直接列D档。
              </p>
            </div>
            <div class="radio-group" style="margin-bottom: 16px;">
              <label class="radio-label">
                <input type="radio" name="otherDGrade" v-model="formData.otherDGrade" value="yes" :disabled="!isAuditMode" />
                <span>列入D档</span>
              </label>
              <label class="radio-label">
                <input type="radio" name="otherDGrade" v-model="formData.otherDGrade" value="no" :disabled="!isAuditMode" />
                <span>不列入D档</span>
              </label>
            </div>

            <div class="rule-box">
              <p class="rule-text">
                <span class="rule-num">③</span>
                评价年度内有媒体负面报道，经查实并造成较大影响的，扣6分。
              </p>
            </div>
            <el-input v-model="formData.otherScore1" placeholder="请输入得分" size="small" style="width: 300px; margin-bottom: 16px;" :disabled="!isAuditMode" />

            <div class="rule-box">
              <p class="rule-text">
                <span class="rule-num">④</span>
                年度评价承诺函；
              </p>
            </div>

            <div class="file-section">
              <div class="section-subtitle">承诺函</div>
              <div class="file-item-row">
                <span class="file-icon"><i class="el-icon-document"></i></span>
                <span class="file-name">年度评价承诺函.pdf</span>
                <span class="file-preview">预览</span>
              </div>
            </div>
          </div>

          <!-- ==================== 9-审核结果 ==================== -->
          <div v-else-if="activeSection === 'result'" class="section-content">
            <div class="section-subtitle" style="font-size: 15px; font-weight: 600; margin-bottom: 16px;">审核结果</div>
            
            <div class="radio-group" style="margin-bottom: 16px;">
              <label class="radio-label">
                <input type="radio" name="result1" v-model="formData.result1" value="pass" :disabled="!isAuditMode" />
                <span>通过</span>
              </label>
              <label class="radio-label">
                <input type="radio" name="result1" v-model="formData.result1" value="reject" :disabled="!isAuditMode" />
                <span>驳回</span>
              </label>
            </div>

            <div style="margin-bottom: 16px;">
              <div class="section-subtitle" style="font-weight: 500;">驳回指标</div>
              <el-select v-model="formData.rejectIndex" placeholder="选择指标" size="small" style="width: 300px;" :disabled="!isAuditMode">
                <el-option label="基础指标" value="basic" />
                <el-option label="产业发展" value="industry" />
                <el-option label="企业培育" value="enterprise" />
                <el-option label="科技创新" value="tech" />
                <el-option label="服务能力" value="service" />
                <el-option label="效益产出" value="benefit" />
                <el-option label="安全生产" value="safety" />
                <el-option label="其他" value="other" />
              </el-select>
            </div>

            <div>
              <div class="section-subtitle" style="font-weight: 500;">审核意见</div>
              <el-input
                v-model="formData.resultOpinion"
                type="textarea"
                :rows="6"
                placeholder="请输入您的意见"
                maxlength="500"
                show-word-limit
                class="opinion-textarea"
                :disabled="!isAuditMode"
              />
            </div>

            </div>
        </div>

        <div class="section-nav-footer">
          <el-button
            size="small"
            :disabled="!hasPrevSection"
            @click="goToPrevSection"
          >上一步</el-button>
          <span class="nav-progress">{{ currentSectionNum }} / {{ navItems.length }}</span>
          <el-button
            v-if="isAuditMode && !hasNextSection"
            type="primary"
            size="small"
            @click="handleAuditComplete"
          >审核完成</el-button>
          <el-button
            v-else
            size="small"
            :disabled="!hasNextSection"
            @click="goToNextSection"
          >下一步</el-button>
        </div>
      </div>
    </div>

    <div v-if="showAuditRecord" class="audit-record-panel" @click.self="showAuditRecord = false">
      <div class="panel-content">
        <div v-for="(item, index) in auditHistory" :key="index" :class="['history-item', item.active ? 'active' : '']">
          <div class="history-content">{{ item.content }}</div>
          <div class="history-time">{{ item.time }}</div>
        </div>
      </div>
    </div>
  </div>
</template>

<script>
import { getAuditDetail, updateAuditDetail, getAuditHistory } from '@/api/audit'

export default {
  name: 'AdminAuditDetail',
  data() {
    const navItems = [
      { index: 'basic', label: '1-基础指标' },
      { index: 'industry', label: '2-产业发展' },
      { index: 'enterprise', label: '3-企业培育' },
      { index: 'tech', label: '4-科技创新' },
      { index: 'service', label: '5-服务能力' },
      { index: 'benefit', label: '6-效益产出' },
      { index: 'safety', label: '7-安全生产' },
      { index: 'other', label: '8-其他' },
      { index: 'result', label: '9-审核结果' }
    ]
    return {
      activeSection: 'basic',
      showAuditRecord: false,
      saveLoading: false,
      detailLoading: false,
      navItems,
      visitedSections: { basic: true },
      formData: {
        basicResult1: '',
        enterpriseScore: '',
        enterpriseOpinion: '',
        techOpinion: '',
        techScore2: '',
        techOpinion2: '',
        serviceScore1: '',
        serviceScore2: '',
        serviceScore3: '',
        serviceOpinion1: '',
        serviceScore4: '',
        serviceOpinion2: '',
        serviceScore5: '',
        serviceScore6: '',
        serviceOpinion3: '',
        serviceScore7: '',
        serviceOpinion4: '',
        benefitScore: '',
        benefitOpinion: '',
        safetyScore1: '',
        safetyScore2: '',
        safetyScore3: '',
        safetyScore4: '',
        safetyDGrade: 'no',
        otherDGrade: 'no',
        otherScore1: '',
        result1: '',
        rejectIndex: '',
        resultOpinion: ''
      },
      // 附件数据
      highTechFileList: [],
      enterpriseFileList: [],
      techFileList: [],
      // 表格数据（产业发展 / 科技创新）
      industryTableData: [],
      techTableData: [],
      auditHistory: []
    }
  },
  computed: {
    isAuditMode() {
      return this.$route.query.mode === 'audit'
    },
    allSectionsVisited() {
      return this.navItems.every(item => this.visitedSections[item.index])
    },
    sectionTitle() {
      const titles = {
        basic: '基础指标',
        industry: '产业发展',
        enterprise: '企业培育',
        tech: '科技创新',
        service: '服务能力',
        benefit: '效益产出',
        safety: '安全生产',
        other: '其他',
        result: '审核结果'
      }
      return titles[this.activeSection] || ''
    },
    currentSectionNum() {
      const idx = this.navItems.findIndex(item => item.index === this.activeSection)
      return idx === -1 ? 1 : idx + 1
    },
    hasPrevSection() {
      return this.navItems.findIndex(item => item.index === this.activeSection) > 0
    },
    hasNextSection() {
      const idx = this.navItems.findIndex(item => item.index === this.activeSection)
      return idx < this.navItems.length - 1
    }
  },
  created() {
    this.loadDetail()
    this.loadAuditHistory()
  },
  methods: {
    async loadDetail() {
      const id = this.$route.params.id
      if (!id) return
      this.detailLoading = true
      try {
        const res = await getAuditDetail(id)
        if (res && res.data && Object.keys(res.data).length > 2) {
          const data = res.data
          this.formData = { ...this.formData, ...data }
          this.highTechFileList = Array.isArray(data.highTechFileList) ? data.highTechFileList : []
          this.enterpriseFileList = Array.isArray(data.enterpriseFileList) ? data.enterpriseFileList : []
          this.techFileList = Array.isArray(data.techFileList) ? data.techFileList : []
          this.industryTableData = Array.isArray(data.industryTableData) ? data.industryTableData : []
          this.techTableData = Array.isArray(data.techTableData) ? data.techTableData : []
        } else {
          this.applyMockDetail(id)
        }
      } catch (e) {
        console.error('加载审核详情失败', e)
        this.applyMockDetail(id)
      } finally {
        this.detailLoading = false
      }
    },
    applyMockDetail(id) {
      this.formData = {
        ...this.formData,
        basicResult1: '通过',
        enterpriseScore: '4',
        enterpriseOpinion: '企业培育情况良好，符合要求',
        techOpinion: '科技创新能力较强',
        techScore2: '3',
        techOpinion2: '科研合作成果显著',
        serviceScore1: '4',
        serviceScore2: '5',
        serviceScore3: '3',
        serviceOpinion1: '助企服务机制完善',
        serviceScore4: '4',
        serviceOpinion2: '数字化建设较好',
        serviceScore5: '2',
        serviceScore6: '3',
        serviceOpinion3: '服务活动开展正常',
        serviceScore7: '2',
        serviceOpinion4: '合作项目推进顺利',
        benefitScore: '6',
        benefitOpinion: '亩均效益达到全市平均水平',
        safetyScore1: '',
        safetyScore2: '',
        safetyScore3: '',
        safetyScore4: '',
        safetyDGrade: 'no',
        otherDGrade: 'no',
        otherScore1: '',
        result1: '',
        rejectIndex: '',
        resultOpinion: ''
      }
      this.highTechFileList = [
        { name: '2025年高新技术企业名单.pdf', url: '' }
      ]
      this.techFileList = [
        { name: '科研合作项目材料.pdf', url: '' }
      ]
      this.industryTableData = [
        { parkName: '盛惠哈源科创园', enterpriseName: '杭州某科技有限公司', creditCode: '91330100MA2XXXXXXX', entryTime: '2025.01-至今', address: '杭州市余杭区XX路XX号' },
        { parkName: '盛惠哈源科创园', enterpriseName: '浙江某生物医药有限公司', creditCode: '91330100MA2YYYYYYY', entryTime: '2025.03-至今', address: '杭州市余杭区XX路XX号' }
      ]
      this.techTableData = [
        { level: 'C类', name: '张三', date: '2025-06', company: '杭州某科技有限公司', score: '' },
        { level: 'D类', name: '李四', date: '2025-09', company: '浙江某生物医药有限公司', score: '' }
      ]
    },

    async loadAuditHistory() {
      const id = this.$route.params.id
      if (!id) return
      try {
        const res = await getAuditHistory(id)
        if (res && res.data && Array.isArray(res.data) && res.data.length > 0) {
          this.auditHistory = res.data
        } else {
          this.auditHistory = [
            { content: '园区端提交评价材料', time: '2026-06-01 09:00:00', active: false },
            { content: '区县管理员审核通过', time: '2026-06-10 14:30:00', active: false },
            { content: '待市级管理员审核', time: '2026-06-10 14:30:00', active: true }
          ]
        }
      } catch (e) {
        console.error('获取审核历史失败', e)
        this.auditHistory = [
          { content: '园区端提交评价材料', time: '2026-06-01 09:00:00', active: false },
          { content: '区县管理员审核通过', time: '2026-06-10 14:30:00', active: false },
          { content: '待市级管理员审核', time: '2026-06-10 14:30:00', active: true }
        ]
      }
    },

    handleSectionChange(index) {
      this.activeSection = index
      this.$set(this.visitedSections, index, true)
    },
    handleMenuSelect(index) {
      if (this.isAuditMode && !this.visitedSections[index]) {
        this.$message.warning('请按顺序逐项审核，已完成的可自由跳转')
        return
      }
      this.handleSectionChange(index)
    },
    goToPrevSection() {
      const curIdx = this.navItems.findIndex(item => item.index === this.activeSection)
      if (curIdx > 0) {
        this.handleSectionChange(this.navItems[curIdx - 1].index)
      }
    },
    goToNextSection() {
      const curIdx = this.navItems.findIndex(item => item.index === this.activeSection)
      if (curIdx < this.navItems.length - 1) {
        const nextIndex = this.navItems[curIdx + 1].index
        if (this.visitedSections[nextIndex]) {
          this.handleSectionChange(nextIndex)
        } else {
          this.$confirm('确认已完成当前页面的审核，进入下一页？', '提示', {
            confirmButtonText: '确认进入下一页',
            cancelButtonText: '继续审核',
            type: 'info'
          }).then(() => {
            this.handleSectionChange(nextIndex)
          }).catch(() => {})
        }
      }
    },
    toggleAuditRecord() {
      this.showAuditRecord = !this.showAuditRecord
    },
    goBackToList() {
      this.$router.push('/admin/audit')
    },
    async handleSaveDraft() {
      try {
        await this.$confirm('确认保存当前审核进度？保存后可随时返回继续审核。', '保存确认', {
          confirmButtonText: '确认保存',
          cancelButtonText: '取消',
          type: 'info'
        })
      } catch (e) {
        return
      }
      const id = this.$route.params.id
      if (!id) {
        this.$message.warning('缺少评价记录ID')
        return
      }
      this.saveLoading = true
      try {
        await updateAuditDetail({ id, ...this.formData })
        this.$message.success('已保存审核进度')
        this.$router.push('/admin/audit')
      } catch (e) {
        console.error('保存失败', e)
      } finally {
        this.saveLoading = false
      }
    },
    async handleAuditComplete() {
      const sectionChecks = [
        { label: '3-企业培育', field: 'enterpriseScore', index: 'enterprise' },
        { label: '4-科技创新', field: 'techScore2', index: 'tech' },
        { label: '5-服务能力', field: 'serviceScore1', index: 'service' },
        { label: '6-效益产出', field: 'benefitScore', index: 'benefit' },
        { label: '7-安全生产', field: 'safetyScore1', index: 'safety' },
        { label: '8-其他', field: 'otherScore1', index: 'other' },
        { label: '9-审核结果', field: 'result1', index: 'result' }
      ]
      const uncompleted = sectionChecks
        .filter(item => !this.formData[item.field] || this.formData[item.field] === '')
      if (uncompleted.length > 0) {
        const h = this.$createElement
        const msg = h('div', { style: { lineHeight: '2.2' } }, [
          h('p', { style: { marginBottom: '10px', color: '#303133', fontSize: '14px' } }, '以下子页面尚未完成审核，请填写打分后再提交：'),
          h('div', { style: { background: '#F5F7FA', borderRadius: '6px', padding: '10px 14px' } },
            uncompleted.map(item =>
              h('p', {
                style: { color: '#409EFF', cursor: 'pointer', margin: '4px 0', fontSize: '13px' },
                on: {
                  click: () => {
                    const el = document.querySelector('.el-message-box__wrapper')
                    if (el) {
                      const closeBtn = el.querySelector('.el-message-box__headerbtn')
                      if (closeBtn) closeBtn.click()
                    }
                    this.$nextTick(() => {
                      this.handleSectionChange(item.index)
                    })
                  }
                }
              }, '→ ' + item.label)
            )
          )
        ])
        this.$msgbox({
          title: '审核未完成',
          message: msg,
          confirmButtonText: '知道了',
          type: 'warning',
          showClose: true
        }).catch(() => {})
        return
      }
      try {
        await this.$confirm(
          '确认提交审核结果？提交后将进入市级审核流程。',
          '审核完成确认',
          { confirmButtonText: '确认提交', cancelButtonText: '继续检查', type: 'info' }
        )
        const id = this.$route.params.id
        if (!id) {
          this.$message.warning('缺少评价记录ID')
          return
        }
        this.saveLoading = true
        await updateAuditDetail({ id, ...this.formData })
        this.$message.success('审核完成')
        this.$router.push('/admin/audit')
      } catch (e) {
        if (e !== 'cancel') {
          console.error('审核提交失败', e)
        }
      } finally {
        this.saveLoading = false
      }
    },
  }
}
</script>

<style scoped>
.audit-detail-container {
  padding: 16px 20px 20px;
  background: #F5F7FA;
  height: 100%;
  overflow: hidden;
  display: flex;
  flex-direction: column;
}

.breadcrumb-bar {
  background: #FFFFFF;
  padding: 12px 16px;
  border-radius: 4px;
  margin-bottom: 12px;
  display: flex;
  align-items: center;
  font-size: 13px;
  color: #6B7280;
  flex-shrink: 0;
}

.breadcrumb-link {
  cursor: pointer;
  color: #409EFF;
  font-size: 13px;
}

.breadcrumb-link:hover {
  text-decoration: underline;
}

.breadcrumb-sep {
  margin: 0 8px;
  color: #909399;
}

.breadcrumb-current {
  color: #111827;
  font-weight: 500;
}

.detail-layout {
  display: flex;
  background: #FFFFFF;
  border-radius: 4px;
  flex: 1;
  overflow: hidden;
  min-height: 0;
}

/* ===== 左侧章节导航 ===== */
.side-nav {
  width: 180px;
  border-right: 1px solid #E8EDF5;
  background: #FAFBFC;
}

.nav-menu {
  border: none;
  height: 100%;
}

.nav-menu .el-menu-item {
  height: 44px;
  line-height: 44px;
  font-size: 13px;
  padding-left: 20px;
  color: #303133;
}

.nav-menu .el-menu-item:hover {
  background: #E8F4FD;
}

.nav-menu .el-menu-item.is-active {
  background: #1E40AF;
  color: #FFFFFF;
}

.nav-menu .el-menu-item.is-visited {
  color: #67C23A;
  position: relative;
}

.nav-menu .el-menu-item.is-visited::before {
  content: '';
  position: absolute;
  left: 0;
  top: 8px;
  bottom: 8px;
  width: 3px;
  background: #67C23A;
  border-radius: 0 2px 2px 0;
}

.nav-menu .el-menu-item.is-visited.is-active {
  color: #FFFFFF;
}

.nav-menu .el-menu-item.is-visited.is-active::before {
  background: #A0D9A0;
}

.nav-menu.is-locked .el-menu-item:not(.is-visited):not(.is-active) {
  cursor: not-allowed;
  opacity: 0.5;
}

/* ===== 上一步/下一步导航 ===== */
.section-nav-footer {
  display: flex;
  justify-content: center;
  align-items: center;
  gap: 16px;
  padding: 16px 20px;
  border-top: 1px solid #E8EDF5;
  background: #FAFBFC;
  flex-shrink: 0;
}

.nav-progress {
  font-size: 13px;
  color: #909399;
}

/* ===== 主内容区 ===== */
.main-content {
  flex: 1;
  display: flex;
  flex-direction: column;
}

.content-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 16px 20px;
  border-bottom: 1px solid #E8EDF5;
}

.section-title {
  display: flex;
  align-items: center;
}

.title-icon {
  color: #1E40AF;
  font-weight: bold;
  margin-right: 8px;
  font-size: 16px;
}

.title-text {
  font-size: 15px;
  font-weight: 600;
  color: #111827;
}

.header-actions {
  display: flex;
  align-items: center;
  gap: 10px;
}

.audit-record-btn {
  display: flex;
  align-items: center;
  gap: 6px;
  padding: 6px 12px;
  font-size: 13px;
  color: #606266;
  cursor: pointer;
  border-radius: 4px;
  transition: background 0.2s;
}

.audit-record-btn:hover {
  background: #F5F7FA;
}

.content-body {
  flex: 1;
  padding: 20px;
  overflow-y: auto;
}

.section-content {
  min-height: 300px;
}

/* ===== 规则说明框 ===== */
.rule-box {
  background: #F5F7FA;
  border-radius: 4px;
  padding: 16px;
  margin-bottom: 20px;
}

.rule-text {
  font-size: 13px;
  color: #303133;
  line-height: 1.8;
  margin: 0 0 10px 0;
}

.rule-text:last-child {
  margin-bottom: 0;
}

.rule-num {
  display: inline-block;
  width: 20px;
  height: 20px;
  background: #409EFF;
  color: #FFFFFF;
  border-radius: 50%;
  text-align: center;
  font-size: 12px;
  line-height: 20px;
  margin-right: 8px;
  font-weight: 500;
}

/* ===== 单选按钮组 ===== */
.radio-group {
  margin-bottom: 20px;
}

.radio-label {
  margin-right: 24px;
  font-size: 13px;
  color: #303133;
  cursor: pointer;
}

.radio-label input {
  margin-right: 6px;
}

/* ===== 空数据/空文件 ===== */
.data-empty,
.file-empty {
  font-size: 13px;
  color: #909399;
  padding: 16px;
  text-align: center;
  background: #FAFBFC;
  border-radius: 4px;
  border: 1px dashed #E8EDF5;
}

/* ===== 文件预览不可用 ===== */
.file-preview-disabled {
  color: #c0c4cc;
  font-size: 13px;
}

/* ===== 行内标签与值 ===== */
.field-label-inline {
  font-size: 13px;
  color: #606266;
  margin-right: 4px;
}

.field-value-inline {
  font-size: 13px;
  color: #303133;
  margin-right: 16px;
}

/* ===== 文件区域 ===== */
.file-section {
  background: #FFFFFF;
  border: 1px solid #E8EDF5;
  border-radius: 4px;
  padding: 16px;
}

.section-subtitle {
  font-size: 13px;
  font-weight: 600;
  color: #303133;
  margin-bottom: 12px;
}

.file-item-row {
  display: flex;
  align-items: center;
  gap: 10px;
  padding: 8px 0;
  border-bottom: 1px dashed #E8EDF5;
}

.file-item-row:last-child {
  border-bottom: none;
}

.file-icon {
  font-size: 16px;
  color: #409EFF;
}

.file-name {
  font-size: 13px;
  color: #303133;
  max-width: 400px;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
  flex: 0 0 auto;
}

.file-name-trunc {
  font-size: 13px;
  color: #303133;
  max-width: 280px;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}

.file-preview {
  color: #409EFF;
  font-size: 13px;
  cursor: pointer;
}

.file-preview:hover {
  text-decoration: underline;
}

.score-input-inline {
  margin-left: auto;
}

.score-label-inline {
  font-size: 13px;
  color: #606266;
  margin-right: 4px;
}

/* ===== 意见区域 ===== */
.opinion-section {
  margin-top: 20px;
}

.opinion-textarea {
  width: 100%;
}

/* ===== 表格 ===== */
.audit-table {
  margin-top: 10px;
}

.audit-table .el-table__header th {
  background: #FAFBFC;
  font-weight: 600;
  color: #303133;
  font-size: 13px;
}

.audit-table .el-table__body td {
  font-size: 12px;
}

/* ===== 科技创新人才行 ===== */
.tech-rows {
  margin-top: 10px;
}

.tech-row {
  display: flex;
  align-items: center;
  gap: 8px 16px;
  padding: 12px 16px;
  border: 1px solid #E8EDF5;
  border-radius: 4px;
  margin-bottom: 10px;
  background: #FFFFFF;
  flex-wrap: wrap;
}

.tech-row-cell {
  display: inline-flex;
  align-items: center;
  flex-shrink: 0;
}

.tech-score-wrap {
  margin-left: auto;
}

/* ===== 双栏布局（服务能力） ===== */
.two-col-section {
  display: flex;
  gap: 16px;
  margin-top: 12px;
}

.two-col-section .col-item {
  flex: 1;
  background: #FFFFFF;
  border: 1px solid #E8EDF5;
  border-radius: 4px;
  padding: 16px;
}

.two-col-section .file-item-row {
  border-bottom: none;
}

/* ===== 得分汇总行 ===== */
.score-summary-row {
  display: flex;
  align-items: center;
  gap: 12px;
  padding: 6px 0;
}

.score-label {
  font-size: 13px;
  color: #303133;
  font-weight: 500;
  min-width: 70px;
}

/* ===== 审核记录面板 ===== */
.audit-record-panel {
  position: fixed;
  top: 0;
  right: 0;
  bottom: 0;
  left: 0;
  z-index: 1000;
  background: rgba(0, 0, 0, 0);
  display: flex;
  justify-content: flex-end;
}

.panel-content {
  width: 400px;
  max-height: 100%;
  overflow-y: auto;
  background: #FFFFFF;
  box-shadow: -2px 0 12px rgba(0, 0, 0, 0.1);
  padding: 16px;
}

.history-item {
  padding: 12px 0;
  border-bottom: 1px solid #EBEEF5;
}

.history-item:last-child {
  border-bottom: none;
}

.history-item.active {
  background: #FAFCFF;
  padding: 12px;
  margin: 0 -16px;
  padding-left: 16px;
}

.history-content {
  font-size: 13px;
  color: #303133;
  line-height: 1.5;
}

.history-time {
  font-size: 12px;
  color: #909399;
  margin-top: 4px;
}

/* ===== 响应式 ===== */
@media (max-width: 1400px) {
  .side-nav {
    width: 160px;
  }

  .file-name {
    max-width: 300px;
  }

  .tech-row {
    flex-wrap: wrap;
  }
}

@media (max-width: 992px) {
  .detail-layout {
    flex-direction: column;
  }

  .side-nav {
    width: 100%;
    border-right: none;
    border-bottom: 1px solid #E8EDF5;
  }

  .nav-menu {
    display: flex;
    flex-wrap: wrap;
  }

  .nav-menu .el-menu-item {
    width: 33.33%;
    text-align: center;
    padding-left: 0;
  }

  .file-name {
    max-width: 200px;
  }

  .two-col-section {
    flex-direction: column;
  }

  .panel-content {
    width: 100%;
  }
}
</style>