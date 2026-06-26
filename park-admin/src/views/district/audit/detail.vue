<template>
  <div class="audit-detail">
    <!-- 文件预览对话框 -->
    <el-dialog 
      :title="previewDialogTitle" 
      :visible.sync="previewDialogVisible" 
      width="800px"
      :close-on-click-modal="false"
      :show-close="true"
      class="preview-dialog"
    >
      <div class="preview-content">
        <div class="preview-document">
          <p class="document-title">承诺书</p>
          <p class="document-content">
            根据《关于开展2024年度小微企业园绩效评价工作的通知》文件精神，我公司向贵单位申请开展2024年度小微企业园绩效评价工作。现就我单位提供的企业清单信息和其他相关佐证材料情况承诺如下：
          </p>
          <p class="document-content">
            一、我单位对提供的企业清单信息和其他相关佐证材料的真实性、合法性、完整性负责，具体建立在下列基础上：
          </p>
          <p class="document-content">
            (一)提供的企业清单信息和其他相关佐证材料是合法、真实、完整的，全面、准确反映了我单位2024年度整体园区运营及企业服务等相关情况。
          </p>
          <p class="document-content">
            (二)提供的证件、合同、合同、法律文书、本单位重大经济决策的会议记录(纪要)、决议及有关文件是真实、完整、通过合法途径取得并仍生效的。
          </p>
          <p class="document-content">
            (三)提供的复印件未作任何修改，并已与原件核对无误。
          </p>
          <p class="document-content">
            二、如提供资料和情况与上述承诺不符，我单位愿承担全部的法律责任。
          </p>
          <p class="document-content">
            二、本单位无不良信用记录，当年度未发生安全生产、食品安全、环境污染等重大责任事故、较大群体性事件及其他禁止申报政府扶持资金的行为。
          </p>
          <p class="document-content">
            三、本单位无不良信用记录，当年度未发生安全生产、食品安全、环境污染等重大责任事故、较大群体性事件及其他禁止申报政府扶持资金的行为。
          </p>
          <p class="document-content">
            单位(盖章) 法定代表人(签名):
          </p>
          <p class="document-content">
            单位负责人(签名):
          </p>
          <p class="document-content">
            年 月 日
          </p>
          <p class="document-content">
            年 月 日
          </p>
        </div>
      </div>
      <div class="preview-footer">
        <el-button @click="closePreviewDialog">关闭</el-button>
        <el-button type="primary" @click="downloadFile">下载原文件</el-button>
      </div>
    </el-dialog>

    <div class="main-content">
      <!-- 左侧导航 -->
      <div class="left-sidebar">
        <el-menu :default-active="activeIndex" mode="vertical" class="audit-menu">
          <el-menu-item 
            v-for="item in menuItems" 
            :key="item.index" 
            :index="item.index"
            @click="handleMenuClick(item.index)"
          >
            <span class="step-number" :class="{ 'completed': parseInt(item.index) < parseInt(activeIndex) }">{{ item.index }}</span>
            <span>{{ item.label }}</span>
          </el-menu-item>
        </el-menu>
      </div>

      <!-- 右侧内容区 -->
      <div class="right-content">
        <!-- 顶部操作栏 -->
        <div class="top-bar">
          <div class="top-bar-left">
            <h2 class="page-title">{{ evaluationInfo.parkName || '园区评价审核' }}</h2>
          </div>
          <div class="top-bar-right">
            <el-button type="primary" @click="viewAuditRecords">审核记录</el-button>
          </div>
        </div>
        <div v-show="activeIndex === '1'" class="tab-content">
          <div class="section-header">
            <h3 class="section-title">基础指标</h3>
          </div>
          <div class="tip-box success">
            <i class="el-icon-check-circle"></i>
            <span>评价年度内参评园区需符合《杭州市升级版小微企业园区建设和管理工作指引（试行）》明确的小微企业园认定条件，不具备的直接判D档。</span>
          </div>
          <div class="form-section checkbox-section">
            <el-checkbox v-model="auditForm.basicConfirm" disabled>我已知晓</el-checkbox>
          </div>
          <div class="bottom-actions">
            <el-button type="primary" @click="handleNextStep">下一步</el-button>
          </div>
        </div>

        <!-- 产业发展 -->
        <div v-show="activeIndex === '2'" class="tab-content">
          <div class="section-header">
            <h3 class="section-title">产业发展</h3>
          </div>
          <div class="tip-box info">
            <span>①园区产业围绕"296X"先进制造业集群集聚发展，并以此形成主导产业的，得5分。</span>
          </div>
          <div class="tip-box info">
            <span>②园区主导产业明确，效益突出，评价年度内主导产业产值（营收）及入驻企业数占比均超过50%，两项占比在50%的基础上每增加10%，得10分。</span>
          </div>
          <div class="tip-box info">
            <span>③评价年度内园区服务类企业不少于20家，产值占比不低于60%，产值占比在60%的基础上每增加10%，得10分。</span>
          </div>
          <div class="tip-box info">
            <span>④评价年度内园区注册企业列表名单。</span>
          </div>
          <div class="table-wrapper">
            <el-table :data="enterpriseList" border style="width: 100%;" max-height="400px">
              <el-table-column prop="rowIndex" label="序号" width="60" align="center" />
              <el-table-column prop="parkName" label="园区名称" min-width="120" />
              <el-table-column prop="enterpriseName" label="入驻企业名称" min-width="200" />
              <el-table-column prop="creditCode" label="统一社会信用代码" min-width="180" />
              <el-table-column prop="settledDate" label="入驻起止时间" min-width="150" />
              <el-table-column prop="registeredAddress" label="企业注册地址" min-width="200" />
            </el-table>
          </div>
          <div class="bottom-actions">
            <el-button @click="handlePrevStep">上一步</el-button>
            <el-button type="primary" @click="handleNextStep">下一步</el-button>
          </div>
        </div>

        <!-- 企业培育 -->
        <div v-show="activeIndex === '3'" class="tab-content">
          <div class="section-header">
            <h3 class="section-title">企业培育</h3>
          </div>
          <div class="tip-box info">
            <span>①园区内存量的规模以上工业企业每家得1分；评价年度内园区每新增1家规模以上工业企业得2分；评价年度内有规模以上工业企业退到规下的，每退减1家减1分。</span>
          </div>
          <div class="tip-box info">
            <span>②评价年度内园区内企业每新增一家国家级制造业单项冠军得4分；每新增一家上市企业得3分；每新增一家国家级专精特新小巨人或省级隐形冠军企业得2分；每新增一家省专精特新中小企业或国家高新技术企业得1分；每新增一家创新型中小企业得0.5分。同一企业不重复计算。</span>
          </div>
          <div class="tip-box info">
            <span>③园区建立专属产业基金，评价年度内园区内企业"投早投小投创新"案例，每新增1个得1分。</span>
          </div>
          <div class="file-section">
            <div class="file-title">产业发展数据模板</div>
            <div v-if="!enterpriseFileList || enterpriseFileList.length === 0" class="empty-file-tip">园区端未上传文件</div>
            <div v-else v-for="(file, idx) in enterpriseFileList" :key="'ent-'+idx" class="file-item">
              <i class="el-icon-file-text"></i>
              <span>{{ file.fileName }}</span>
              <a href="javascript:void(0)" class="file-action" @click="handleFilePreview(file)">预览</a>
            </div>
          </div>
          <div v-if="cultivationList && cultivationList.length > 0" class="table-wrapper" style="margin-top:12px;">
            <el-table :data="cultivationList" border style="width: 100%;" max-height="300px">
              <el-table-column label="项目名称" min-width="180">
                <template slot-scope="scope">{{ scope.row.projectName || scope.row.fileName || '-' }}</template>
              </el-table-column>
              <el-table-column label="附件" min-width="200">
                <template slot-scope="scope">
                  <a href="javascript:void(0)" v-if="scope.row.fileUrl" @click="handleFilePreview(scope.row)">{{ scope.row.fileName || '查看附件' }}</a>
                  <span v-else>{{ scope.row.fileName || '-' }}</span>
                </template>
              </el-table-column>
            </el-table>
          </div>
          <div class="bottom-actions">
            <el-button @click="handlePrevStep">上一步</el-button>
            <el-button type="primary" @click="handleNextStep">下一步</el-button>
          </div>
        </div>

        <!-- 科技创新 -->
        <div v-show="activeIndex === '4'" class="tab-content">
          <div class="section-header">
            <h3 class="section-title">科技创新</h3>
          </div>
          <div class="tip-box info">
            <span>①评价年度内园区内企业新增省级及以上首台（套）装备、首版次、首批次、省级优秀工业新产品、浙江制造精品的，每项得2分。</span>
          </div>
          <div class="tip-box info">
            <span>②评价年度内园区新增国家级、省级、市级企业研发机构的，每项分别得3分、2分、1分。同一企业（机构）不重复计算。</span>
          </div>
          <div class="tip-box info">
            <span>③评价年度内园区新引进独立注册的市级及以上科研创新、企业孵化及检验检测服务等公共服务平台的，每项得2分。同一项目不重复计算。</span>
          </div>
          <div class="tip-box info">
            <span>④根据杭州市高层次人才分类目录标准，评价年度内园区企业新认定A、B、C、D类人才的，每人分别得3分、2分、1.5分、1分。</span>
          </div>
          <div class="file-section">
            <div class="file-title">科技创新附件</div>
            <div v-if="!talentList || talentList.length === 0" class="empty-file-tip">园区端未上传文件</div>
            <div v-else v-for="(item, idx) in talentList" :key="'tech-'+idx" class="file-item tech-file-item">
              <i class="el-icon-file-text"></i>
              <div class="tech-file-info">
                <div class="tech-file-name">{{ item.fileName || '附件' + (idx + 1) }}</div>
                <div class="tech-file-meta">
                  <span v-if="item.projectName">项目：{{ item.projectName }}</span>
                  <span v-if="item.level" class="meta-sep">｜</span>
                  <span v-if="item.level">类别：{{ item.level }}</span>
                  <span v-if="item.name" class="meta-sep">｜</span>
                  <span v-if="item.name">姓名：{{ item.name }}</span>
                  <span v-if="item.certDate" class="meta-sep">｜</span>
                  <span v-if="item.certDate">认定日期：{{ item.certDate }}</span>
                  <span v-if="item.company" class="meta-sep">｜</span>
                  <span v-if="item.company">企业：{{ item.company }}</span>
                </div>
              </div>
              <a href="javascript:void(0)" class="file-action" @click="handleFilePreview(item)">预览</a>
            </div>
          </div>
          <div class="tip-box info">
            <span>⑤园区与科研院所建立合作关系，在园区开展科研成果转化并在评价年度形成500万元以上产出的，每项得1分。</span>
          </div>
          <div v-if="techProjectList && techProjectList.length > 0" class="table-wrapper">
            <div class="file-title" style="margin-bottom:8px;">院所合作项目</div>
            <el-table :data="techProjectList" border style="width: 100%;" max-height="200px">
              <el-table-column prop="projectName" label="合作项目" min-width="200" />
              <el-table-column label="附件" min-width="200">
                <template slot-scope="scope">
                  <a href="javascript:void(0)" v-if="scope.row.fileUrl" @click="handleFilePreview(scope.row)">{{ scope.row.fileName || '查看附件' }}</a>
                  <span v-else>{{ scope.row.fileName || '-' }}</span>
                </template>
              </el-table-column>
            </el-table>
          </div>
          <div class="bottom-actions">
            <el-button @click="handlePrevStep">上一步</el-button>
            <el-button type="primary" @click="handleNextStep">下一步</el-button>
          </div>
        </div>

        <!-- 服务能力 -->
        <div v-show="activeIndex === '5'" class="tab-content">
          <div class="section-header">
            <h3 class="section-title">服务能力</h3>
          </div>
          <div class="tip-box info">
            <span>①园区建立助企服务站（工作室）并有明确完善助企服务机制的，得5分；对入园企业项目报批实行一站式全程代办服务的，得5分；按规定组织企业开展党员、团员及工会活动的，得5分。</span>
          </div>
          <div class="file-section">
            <div class="file-title">助企服务站建设材料</div>
            <div v-if="!serviceFiles.enterpriseService || serviceFiles.enterpriseService.length === 0" class="empty-file-tip">园区端未上传文件</div>
            <div v-else v-for="(file, idx) in serviceFiles.enterpriseService" :key="'s1-'+idx" class="file-item">
              <i class="el-icon-file-text"></i>
              <span>{{ file.fileName }}</span>
              <a href="javascript:void(0)" class="file-action" @click="handleFilePreview(file)">预览</a>
            </div>
          </div>
          <div class="file-section">
            <div class="file-title">一站式代办服务材料</div>
            <div v-if="!serviceFiles.oneStopService || serviceFiles.oneStopService.length === 0" class="empty-file-tip">园区端未上传文件</div>
            <div v-else v-for="(file, idx) in serviceFiles.oneStopService" :key="'s2-'+idx" class="file-item">
              <i class="el-icon-file-text"></i>
              <span>{{ file.fileName }}</span>
              <a href="javascript:void(0)" class="file-action" @click="handleFilePreview(file)">预览</a>
            </div>
          </div>
          <div class="file-section">
            <div class="file-title">党团工会活动材料</div>
            <div v-if="!serviceFiles.unionActivity || serviceFiles.unionActivity.length === 0" class="empty-file-tip">园区端未上传文件</div>
            <div v-else v-for="(file, idx) in serviceFiles.unionActivity" :key="'s3-'+idx" class="file-item">
              <i class="el-icon-file-text"></i>
              <span>{{ file.fileName }}</span>
              <a href="javascript:void(0)" class="file-action" @click="handleFilePreview(file)">预览</a>
            </div>
          </div>
          <div class="tip-box info">
            <span>②建立园区大脑，并获得省级数字化示范园区的，得5分，未获得省级数字化示范园区，但能集聚园区数据资源，集成相关功能模块，利用实时数据优化园区公共资源，实现园区治理智慧化，助力园区运营升级的，得4分；未建立园区大脑的最高得3分，其中，有信息发布平台、数字化建设等独立系统的，可得1分；已实施数字化物业管理的，可得1分；具有数字化管理专门机构的，可得1分；实现园区安全生产数字化监管的，可得1分。</span>
          </div>
          <div class="file-section">
            <div class="file-title">园区大脑数字化相关资料</div>
            <div v-if="!serviceFiles.parkBrain || serviceFiles.parkBrain.length === 0" class="empty-file-tip">园区端未上传文件</div>
            <div v-else v-for="(file, idx) in serviceFiles.parkBrain" :key="'s4-'+idx" class="file-item">
              <i class="el-icon-file-text"></i>
              <span>{{ file.fileName }}</span>
              <a href="javascript:void(0)" class="file-action" @click="handleFilePreview(file)">预览</a>
            </div>
          </div>
          <div class="tip-box info">
            <span>③评价年度内开展普惠性服务活动10场以上（每场活动参加企业5家以上），得3分；开展针对个性化助企服务活动20次以上的，得3分。</span>
          </div>
          <div class="file-section">
            <div class="file-title">普惠性服务活动 <span class="file-hint">请上传活动"通知+签到+照片"相关资料</span></div>
            <div v-if="!serviceFiles.inclusiveService || serviceFiles.inclusiveService.length === 0" class="empty-file-tip">园区端未上传文件</div>
            <div v-else v-for="(file, idx) in serviceFiles.inclusiveService" :key="'s5-'+idx" class="file-item">
              <i class="el-icon-file-text"></i>
              <span>{{ file.fileName }}</span>
              <a href="javascript:void(0)" class="file-action" @click="handleFilePreview(file)">预览</a>
            </div>
          </div>
          <div class="file-section">
            <div class="file-title">个性化服务活动 <span class="file-hint">请上传活动"照片+说明"相关资料</span></div>
            <div v-if="!serviceFiles.personalizedService || serviceFiles.personalizedService.length === 0" class="empty-file-tip">园区端未上传文件</div>
            <div v-else v-for="(file, idx) in serviceFiles.personalizedService" :key="'s6-'+idx" class="file-item">
              <i class="el-icon-file-text"></i>
              <span>{{ file.fileName }}</span>
              <a href="javascript:void(0)" class="file-action" @click="handleFilePreview(file)">预览</a>
            </div>
          </div>
          <div class="tip-box info">
            <span>④园区与杭州市范围内的其他小微园区形成合作关系，进行管理服务输出，评价年度内取得产业、科创合作3个以上项目的，得3分。</span>
          </div>
          <div class="file-section">
            <div class="file-title">合作项目资料</div>
            <div v-if="!serviceFiles.cooperationProject || serviceFiles.cooperationProject.length === 0" class="empty-file-tip">园区端未上传文件</div>
            <div v-else v-for="(file, idx) in serviceFiles.cooperationProject" :key="'s7-'+idx" class="file-item">
              <i class="el-icon-file-text"></i>
              <span>{{ file.fileName }}</span>
              <a href="javascript:void(0)" class="file-action" @click="handleFilePreview(file)">预览</a>
            </div>
          </div>
          <div class="bottom-actions">
            <el-button @click="handlePrevStep">上一步</el-button>
            <el-button type="primary" @click="handleNextStep">下一步</el-button>
          </div>
        </div>

        <!-- 效益产出 -->
        <div v-show="activeIndex === '6'" class="tab-content">
          <div class="section-header">
            <h3 class="section-title">效益产出</h3>
          </div>
          <div class="tip-box info">
            <span>①评价年度亩均税收达到全市生产性服务业类园区平均水平：1.5-2倍得2分；2-2.5倍得4分；2.5-3倍得6分；3-3.5倍得8分；3.5倍及以上得10分。</span>
          </div>
          <div class="tip-box info">
            <span>②评价年度亩均产出达到全市生产性服务业类园区平均水平：1.5-2倍得2分；2-2.5倍得4分；2.5-3倍得6分；3.5倍及以上得10分。</span>
          </div>
          <div class="tip-box info">
            <span>③贯彻落实集约发展理念，通过改造提升实现工业上楼、效益提升的，得5分。</span>
          </div>
          <div class="file-section">
            <div class="file-title">效益产出相关材料</div>
            <div v-if="!benefitFileList || benefitFileList.length === 0" class="empty-file-tip">园区端未上传文件</div>
            <div v-else v-for="(file, idx) in benefitFileList" :key="'bf-'+idx" class="file-item">
              <i class="el-icon-file-text"></i>
              <span>{{ file.fileName }}</span>
              <a href="javascript:void(0)" class="file-action" @click="handleFilePreview(file)">预览</a>
            </div>
          </div>
          <div class="bottom-actions">
            <el-button @click="handlePrevStep">上一步</el-button>
            <el-button type="primary" @click="handleNextStep">下一步</el-button>
          </div>
        </div>

        <!-- 安全生产 -->
        <div v-show="activeIndex === '7'" class="tab-content">
          <div class="section-header">
            <h3 class="section-title">安全生产</h3>
          </div>
          <div class="tip-box info">
            <span>①未落实《杭州市小微企业园安全管理通则》要求，经查实的，扣2分；</span>
            <div class="score-input-wrapper">
              <span class="score-label">得分：</span>
              <el-input v-model="auditForm.safetyScore1" type="number" placeholder="请输入得分" class="safety-score-input" />
            </div>
          </div>
          <div class="tip-box info">
            <span>②未签订消防安全责任书的，扣2分；</span>
            <div class="score-input-wrapper">
              <span class="score-label">得分：</span>
              <el-input v-model="auditForm.safetyScore2" type="number" placeholder="请输入得分" class="safety-score-input" />
            </div>
          </div>
          <div class="tip-box info">
            <span>③未落实培训、演练要求的，扣2分；</span>
            <div class="score-input-wrapper">
              <span class="score-label">得分：</span>
              <el-input v-model="auditForm.safetyScore3" type="number" placeholder="请输入得分" class="safety-score-input" />
            </div>
          </div>
          <div class="tip-box info">
            <span>④消防设施器材不完整或过期的，扣2分；</span>
            <div class="score-input-wrapper">
              <span class="score-label">得分：</span>
              <el-input v-model="auditForm.safetyScore4" type="number" placeholder="请输入得分" class="safety-score-input" />
            </div>
          </div>
          <div class="tip-box info">
            <span>⑤存在安全隐患被省、市主管部门通报的，每次扣2分。</span>
            <div class="score-input-wrapper">
              <span class="score-label">得分：</span>
              <el-input v-model="auditForm.safetyScore5" type="number" placeholder="请输入得分" class="safety-score-input" />
            </div>
          </div>
          <div class="tip-box info">
            <span>⑥近一年内发生较大以上的安全生产事故或较大影响的社会事件，园区安全生产隐患未按期整改的，直接判D档。</span>
            <div class="radio-group">
              <el-radio v-model="auditForm.safetyResult" label="1">直接判D档</el-radio>
              <el-radio v-model="auditForm.safetyResult" label="2">不直接判D档</el-radio>
            </div>
          </div>
          <div class="tip-box info highlight">
            <span>本小项总分 30.0分</span>
          </div>
          <div class="file-section">
            <div class="file-title">安全生产相关材料</div>
            <div class="file-upload-area">
              <div class="upload-btn-wrapper" @click="handleSafetyFileUpload">
                <i class="el-icon-upload"></i>
                <span>+上传附件</span>
              </div>
              <input type="file" class="hidden-file-input" ref="safetyFileInput" @change="handleSafetyFileChange" accept=".doc,.docx,.xls,.xlsx,.pdf,.png,.jpg,.jpeg" />
            </div>
            <div v-if="safetyFiles.length === 0" class="empty-file-tip">暂无上传文件</div>
            <div v-else class="file-list">
              <div v-for="(file, index) in safetyFiles" :key="index" class="file-item">
                <i class="el-icon-file-text"></i>
                <span class="file-name">{{ file.name }}</span>
                <span class="file-size">{{ file.size }}</span>
                <a href="javascript:void(0)" class="file-action preview" @click="handleFilePreview(file)">预览</a>
                <a href="javascript:void(0)" class="file-action delete" @click="deleteSafetyFile(index)">删除</a>
              </div>
            </div>
          </div>
          <div class="form-section">
            <label class="form-label">意见：</label>
            <el-input v-model="auditForm.safetyOpinion" type="textarea" :rows="3" placeholder="意见" maxlength="500" show-word-limit class="opinion-input" />
          </div>
          <div class="bottom-actions">
            <el-button type="primary" @click="handleSave">保存</el-button>
            <el-button @click="handlePrevStep">上一步</el-button>
            <el-button type="primary" @click="handleNextStep">下一步</el-button>
          </div>
        </div>

        <!-- 其他 -->
        <div v-show="activeIndex === '8'" class="tab-content">
          <div class="section-header">
            <h3 class="section-title">其他</h3>
          </div>
          <div class="tip-box info">
            <span>①园区在浙江省小微企业园信息管理系统中未按时完整报送小微企业园季度运行数据，少报一次减2分；年度数据未报的直接判D档。</span>
          </div>
          <div class="tip-box info">
            <span>②申报材料、报送数据作伪的，运营管理机构被列入杭州市公共信用信息平台失信联合惩戒对象名单的，直接判D档。</span>
          </div>
          <div class="tip-box info">
            <span>③评价年度内有媒体负面报道，经查实并造成较大影响的，扣6分。</span>
          </div>
          <div class="tip-box info">
            <span>④评价年度承诺函。</span>
          </div>
          
          <!-- 园区上传的附件 -->
          <div v-if="otherFileList.length > 0" class="attachment-list">
            <div class="attachment-title">园区上传附件</div>
            <div v-for="(file, index) in otherFileList" :key="index" class="attachment-item">
              <i class="el-icon-document"></i>
              <span class="attachment-name">{{ file.fileName }}</span>
              <span class="action-link preview-link" @click.stop="handleFilePreview(file)">预览</span>
            </div>
          </div>
          
          <!-- 评价结果模块 -->
          <div class="audit-conclusion">
            <div class="conclusion-title">审核结论</div>
            <div class="radio-group">
              <el-radio v-model="auditForm.finalResult" label="1">通过</el-radio>
              <el-radio v-model="auditForm.finalResult" label="2">驳回</el-radio>
            </div>
            <div class="opinion-section">
              <el-input v-model="auditForm.finalOpinion" type="textarea" :rows="3" placeholder="请输入审核意见" maxlength="500" show-word-limit />
            </div>
            <el-button type="primary" class="confirm-btn" @click="showConfirmDialog">确定</el-button>
          </div>
          <div class="bottom-actions">
            <el-button @click="handlePrevStep">上一步</el-button>
          </div>
        </div>

        
      </div>
    </div>

    <!-- 审核确认对话框 -->
    <el-dialog title="二次确认" :visible.sync="confirmDialogVisible" width="450px" :close-on-click-modal="false">
      <div class="confirm-content">
        <div class="confirm-icon">
          <i class="el-icon-check-circle"></i>
        </div>
        <div class="confirm-text">请核实该园区可参评并上报市局。</div>
      </div>
      <div slot="footer" class="dialog-footer">
        <el-button @click="confirmDialogVisible = false">取消</el-button>
        <el-button type="primary" :loading="submitting" @click="confirmAudit">确定</el-button>
      </div>
    </el-dialog>

    <!-- 审核记录对话框 -->
    <AuditTimeline :visible.sync="auditRecordsDialogVisible" :history="auditRecords" />

    <!-- 行文文件预览组件 -->
    <FilePreview :visible.sync="filePreviewVisible" :file-url="previewUrl" :file-name="previewDialogTitle" />
  </div>
</template>

<script>
import { getEvaluationDetail, submitAudit, getAuditHistory, getParkFiles, uploadParkFile, saveEvaluationScore } from '@/api/audit'
import { uploadFile } from '@/api/tech-innovation'
import { mapGetters } from 'vuex'
import FilePreview from '@/components/FilePreview.vue'
import AuditTimeline from '@/components/AuditTimeline.vue'

export default {
  name: 'AuditDetail',
  components: { FilePreview, AuditTimeline },
  data() {
    return {
      activeIndex: '1',
      evaluationInfo: {},
      submitting: false,
      confirmDialogVisible: false,
      confirmDialogTitle: '',
      previewDialogVisible: false,
      previewDialogTitle: '',
      previewUrl: '',
      auditRecordsDialogVisible: false,
      auditRecords: [],
      documentFile: null,
      documentUploading: false,
      filePreviewVisible: false,
      stepCompleted: {
        '1': false,
        '2': false,
        '3': false,
        '4': false,
        '5': false,
        '6': false,
        '7': false,
        '8': false
      },
      menuItems: [
        { index: '1', label: '基础指标' },
        { index: '2', label: '产业发展' },
        { index: '3', label: '企业培育' },
        { index: '4', label: '科技创新' },
        { index: '5', label: '服务能力' },
        { index: '6', label: '效益产出' },
        { index: '7', label: '安全生产' },
        { index: '8', label: '其他' }
      ],
      auditForm: {
        evaluationId: null,
        basicResult: '',
        basicConfirm: false,
        enterpriseScore: '',
        enterpriseOpinion: '',
        techScore: '',
        techOpinion: '',
        techOpinion2: '',
        serviceScore1: '',
        serviceScore2: '',
        serviceScore3: '',
        serviceOpinion: '',
        digitalScore: '',
        digitalOpinion: '',
        publicScore: '',
        personalScore: '',
        activityOpinion: '',
        cooperationScore: '',
        cooperationOpinion: '',
        outputScore1: '',
        outputScore2: '',
        outputOpinion: '',
        safetyScore1: '',
        safetyScore2: '',
        safetyScore3: '',
        safetyScore4: '',
        safetyResult: '',
        otherResult: '',
        mediaScore: '',
        finalResult: '',
        rejectReason: '',
        finalOpinion: ''
      },
      enterpriseList: [],
      talentList: [],
      // 安全生产附件列表
      safetyFiles: [],
      safetyUploading: false,
      // 园区端上传的文件（来自 parkExtraData）- 字段名与园区端 add.vue 保持一致
      serviceFiles: {
        enterpriseService: [], oneStopService: [], unionActivity: [],
        parkBrain: [], inclusiveService: [], personalizedService: [], cooperationProject: []
      },
      benefitFileList: [],
      enterpriseFileList: [],   // 企业培育-产业发展数据模板
      otherFileList: [],        // 其他-承诺函等
      techProjectList: [],      // 院所合作项目
      cultivationList: []       // 企业培育记录
    }
  },
  computed: {
    ...mapGetters(['userInfo'])
  },
  mounted() {
    const id = this.$route.params.id
    if (id) {
      this.fetchDetail(id)
    }
  },
  methods: {
    formatDate(dateStr) {
      if (!dateStr) return ''
      const d = new Date(dateStr)
      if (isNaN(d.getTime())) return dateStr
      const y = d.getFullYear()
      const m = String(d.getMonth() + 1).padStart(2, '0')
      const day = String(d.getDate()).padStart(2, '0')
      return `${y}-${m}-${day}`
    },
    async fetchDetail(id) {
      try {
        const res = await getEvaluationDetail(id)
        this.evaluationInfo = res.data || {}
        this.auditForm.evaluationId = parseInt(id)

        // 填充已有审核数据
        if (this.evaluationInfo.districtOpinion) {
          this.auditForm.finalOpinion = this.evaluationInfo.districtOpinion
        }
        if (this.evaluationInfo.districtResult) {
          this.auditForm.finalResult = String(this.evaluationInfo.districtResult)
        }

        // 从scoreDetailMap填充已有评分数据
        const scoreDetail = this.evaluationInfo.scoreDetailMap || {}
        if (scoreDetail.industryDev) {
          const ind = scoreDetail.industryDev
          if (ind.total != null) this.auditForm.enterpriseScore = String(ind.total)
        }
        if (scoreDetail.techInnovation) {
          const tech = scoreDetail.techInnovation
          if (tech.total != null) this.auditForm.techScore = String(tech.total)
        }

        // 回填安全生产评分（优先使用 districtSafety 快照，其次用 safety）
        const safetyDetail = scoreDetail.districtSafety || scoreDetail.safety
        if (safetyDetail) {
          const sScores = safetyDetail.scores || []
          if (sScores[0] != null) this.auditForm.safetyScore1 = String(sScores[0])
          if (sScores[1] != null) this.auditForm.safetyScore2 = String(sScores[1])
          if (sScores[2] != null) this.auditForm.safetyScore3 = String(sScores[2])
          if (sScores[3] != null) this.auditForm.safetyScore4 = String(sScores[3])
          if (sScores[4] != null) this.auditForm.safetyScore5 = String(sScores[4])
          // dGrade "yes"/"no" → safetyResult "1"(直接判D档)/"2"(不直接判D档)
          if (safetyDetail.dGrade) {
            this.auditForm.safetyResult = safetyDetail.dGrade === 'yes' ? '1' : '2'
          }
          // 回填区县端上传的安全生产文件
          if (Array.isArray(safetyDetail.files) && safetyDetail.files.length > 0) {
            this.safetyFiles = safetyDetail.files.map(f => ({
              name: f.fileName || f.name,
              fileName: f.fileName || f.name,
              fileUrl: f.fileUrl || f.url || '',
              size: ''
            }))
          }
        }

        // 解析园区端提交的 parkExtraData（文件元数据）
        const extra = this.evaluationInfo.extraData || {}
        if (extra.basicAcknowledged) {
          this.auditForm.basicConfirm = extra.basicAcknowledged === 'known'
        }
        if (extra.serviceFiles) {
          this.serviceFiles = Object.assign({}, this.serviceFiles, extra.serviceFiles)
        }
        if (extra.benefitFiles) {
          this.benefitFileList = extra.benefitFiles || []
        }
        if (extra.fileSections && extra.fileSections.otherFiles) {
          this.otherFileList = extra.fileSections.otherFiles || []
        } else if (extra.otherFiles) {
          this.otherFileList = extra.otherFiles || []
        }

        // 产业发展企业列表（按 parkId 关联）
        const rawEnterprises = this.evaluationInfo.enterprises || []
        this.enterpriseList = rawEnterprises.map(item => {
          let settled = item.settledDate
          if (!settled) {
            const start = item.settledStartTime || ''
            const end = item.settledEndTime || ''
            if (start && end) {
              settled = start + ' - ' + end
            } else if (start) {
              settled = start
            } else if (end) {
              settled = end
            } else {
              settled = '-'
            }
          }
          return {
            ...item,
            settledDate: settled,
            registeredAddress: item.registeredAddress || item.enterpriseAddress || item.address || ''
          }
        })
        // 科技创新人才（字段映射：category→level，date→certDate，company→company）
        const rawTalents = this.evaluationInfo.techInnovations || []
        this.talentList = rawTalents.map(item => ({
          level: item.category || '',
          name: item.name || '',
          certDate: item.date ? this.formatDate(item.date) : '',
          company: item.company || '',
          fileUrl: item.fileUrl || '',
          fileName: item.fileName || '',
          projectName: item.projectName || ''
        }))
        // 院所合作项目（字段映射：name→projectName）
        const rawProjects = this.evaluationInfo.techProjects || []
        this.techProjectList = rawProjects.map(item => ({
          projectName: item.name || '',
          fileUrl: item.fileUrl || '',
          fileName: item.fileName || ''
        }))
        // 企业培育记录
        this.cultivationList = this.evaluationInfo.cultivationRecords || []
        // 企业培育文件：从 cultivationRecords 子表提取（园区端存在子表里）
        if (this.cultivationList && this.cultivationList.length > 0) {
          this.enterpriseFileList = this.cultivationList
        }

        // 加载行文文件（区县上传的盖章文件，本年度所有评价材料自动显示）
        if (this.evaluationInfo.parkId) {
          this.loadDocumentFile(this.evaluationInfo.parkId)
        }
      } catch (e) {
        console.error('获取评价详情失败', e)
      }
    },

    // 加载行文文件（只展示最新一次上传的文件）
    async loadDocumentFile(parkId) {
      try {
        const res = await getParkFiles(parkId)
        if (res.code === 200 && res.data && res.data.length > 0) {
          this.documentFile = res.data[0]
        }
      } catch (e) {
        console.warn('加载行文文件失败', e)
      }
    },

    // 上传行文文件
    handleDocumentUpload(file) {
      if (!this.evaluationInfo.parkId) {
        this.$message.error('无法获取园区信息')
        return false
      }
      const allowedTypes = ['.doc', '.docx', '.xls', '.xlsx', '.pdf', '.png', '.jpg', '.jpeg']
      const ext = file.name.substring(file.name.lastIndexOf('.')).toLowerCase()
      if (!allowedTypes.includes(ext)) {
        this.$message.error('支持格式: .doc,.docx,.xls,.xlsx,.pdf,.png,.jpg,.jpeg')
        return false
      }
      if (file.size > 50 * 1024 * 1024) {
        this.$message.error('文件大小不能超过50MB')
        return false
      }
      this.documentUploading = true
      const formData = new FormData()
      formData.append('file', file)
      uploadParkFile(this.evaluationInfo.parkId, formData).then(res => {
        if (res.code === 200 && res.data) {
          this.documentFile = res.data
          this.$message.success('行文文件上传成功，本年度所有评价材料将自动显示此文件')
        }
      }).catch(e => {
        console.error('上传行文文件失败', e)
        this.$message.error('上传失败')
      }).finally(() => {
        this.documentUploading = false
      })
      return false
    },

    // 预览行文文件
    previewDocumentFile() {
      if (this.documentFile && this.documentFile.id) {
        this.previewUrl = `/api/documents/preview/${this.documentFile.id}`
        this.previewDialogTitle = this.documentFile.fileName || '行文文件'
        this.filePreviewVisible = true
      }
    },

    // 预览园区端上传的文件
    handleFilePreview(file) {
      if (!file || !file.fileUrl) {
        this.$message.warning('文件暂无可预览的地址')
        return
      }
      this.previewUrl = file.fileUrl
      this.previewDialogTitle = file.fileName || '文件预览'
      this.filePreviewVisible = true
    },

    // 打开预览对话框
    openPreviewDialog(fileName) {
      this.previewDialogTitle = `文件预览 - ${fileName}`
      this.previewDialogVisible = true
    },

    // 关闭预览对话框
    closePreviewDialog() {
      this.previewDialogVisible = false
    },

    // 下载文件
    downloadFile() {
      this.$message.success('文件下载功能已触发')
    },

    // 安全生产文件上传
    handleSafetyFileUpload() {
      this.$refs.safetyFileInput.click()
    },

    // 处理安全生产文件选择
    handleSafetyFileChange(event) {
      const file = event.target.files[0]
      if (file) {
        // 检查文件大小（50MB）
        const maxSize = 50 * 1024 * 1024
        if (file.size > maxSize) {
          this.$message.error('文件大小不能超过50MB')
          event.target.value = ''
          return
        }
        // 检查文件类型
        const allowedTypes = ['.doc', '.docx', '.xls', '.xlsx', '.pdf', '.png', '.jpg', '.jpeg']
        const ext = file.name.substring(file.name.lastIndexOf('.')).toLowerCase()
        if (!allowedTypes.includes(ext)) {
          this.$message.error('支持格式:.doc,.docx,.xls,.xlsx,.pdf,.png,.jpg,.jpeg')
          event.target.value = ''
          return
        }
        // 转换文件大小显示
        let size = file.size
        let unit = 'B'
        if (size >= 1024 * 1024) {
          size = (size / (1024 * 1024)).toFixed(2)
          unit = 'MB'
        } else if (size >= 1024) {
          size = (size / 1024).toFixed(2)
          unit = 'KB'
        }
        // 实际上传文件到服务器
        const formData = new FormData()
        formData.append('file', file)
        formData.append('bizType', 'industry')
        this.safetyUploading = true
        uploadFile(formData).then(res => {
          const data = res.data || res
          this.safetyFiles.push({
            name: file.name,
            fileName: file.name,
            fileUrl: data.url || data.fileUrl || '',
            size: `${size} ${unit}`
          })
          this.$message.success('文件上传成功')
        }).catch(e => {
          console.error('安全生产文件上传失败', e)
          this.$message.error('文件上传失败')
        }).finally(() => {
          this.safetyUploading = false
          event.target.value = ''
        })
      }
    },

    // 删除安全生产文件
    deleteSafetyFile(index) {
      this.$confirm('确定删除该文件吗？', '确认', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }).then(() => {
        this.safetyFiles.splice(index, 1)
        this.$message.success('删除成功')
      }).catch(() => {})
    },

    // 菜单点击处理
    handleMenuClick(stepIndex) {
      this.activeIndex = String(stepIndex)
    },

    // 检查当前步骤是否完成
    isCurrentStepCompleted() {
      const currentStep = parseInt(this.activeIndex)
      
      switch (currentStep) {
        case 1:
          return this.auditForm.basicResult && this.auditForm.basicConfirm
        case 2:
          return true
        case 3:
          return true
        case 4:
          return true
        case 5:
          return true
        case 6:
          return true
        case 7:
          return this.auditForm.safetyResult
        case 8:
          return this.auditForm.otherResult
        case 9:
          return this.auditForm.finalResult && this.auditForm.finalOpinion
        default:
          return true
      }
    },

    // 下一步处理
    handleNextStep() {
      const currentStep = parseInt(this.activeIndex)
      
      // 标记当前步骤完成
      this.stepCompleted[String(currentStep)] = true
      
      if (currentStep < 8) {
        // 只有最后一步前（步骤7）显示确认提示
        if (currentStep === 7) {
          // 确认提醒
          this.$confirm(`确定要进入第${currentStep + 1}步吗？`, '确认', {
            confirmButtonText: '确定',
            cancelButtonText: '取消',
            type: 'warning'
          }).then(() => {
            this.activeIndex = String(currentStep + 1)
          }).catch(() => {
            // 用户取消，不做任何操作
          })
        } else {
          // 其他步骤直接跳转，不显示确认提示
          this.activeIndex = String(currentStep + 1)
        }
      }
    },

    // 上一步处理
    handlePrevStep() {
      const currentStep = parseInt(this.activeIndex)
      if (currentStep > 1) {
        this.activeIndex = String(currentStep - 1)
      }
    },

    // 点击上传按钮
    handleFileUpload() {
      this.$refs.fileInput.click()
    },

    // 文件选择处理
    handleFileChange(event) {
      const file = event.target.files[0]
      if (file) {
        // 检查文件大小（50MB）
        const maxSize = 50 * 1024 * 1024
        if (file.size > maxSize) {
          this.$message.error('文件大小不能超过50MB')
          return
        }
        // 检查文件类型
        const allowedTypes = ['.doc', '.docx', '.xls', '.xlsx', '.pdf', '.png', '.jpg', '.jpeg']
        const ext = file.name.substring(file.name.lastIndexOf('.')).toLowerCase()
        if (!allowedTypes.includes(ext)) {
          this.$message.error('支持格式:.doc,.docx,.xls,.xlsx,.pdf,.png,.jpg,.jpeg')
          return
        }
        // 模拟上传成功
        this.$message.success('文件上传成功')
        // 清空文件输入
        event.target.value = ''
      }
    },

    // 确认提交处理
    handleConfirmSubmit() {
      // 这里可以添加额外的提交逻辑
      this.showConfirmDialog()
    },

    // 显示确认对话框
    showConfirmDialog() {
      if (!this.auditForm.finalResult) {
        this.$message.warning('请选择审核结果')
        return
      }
      
      this.confirmDialogTitle = this.auditForm.finalResult === '1' ? '审核通过' : '审核驳回'
      this.confirmDialogVisible = true
    },

    // 确认审核
    async confirmAudit() {
      if (!this.auditForm.finalResult) {
        this.$message.warning('请选择审核结果')
        return
      }
      if (!this.auditForm.finalOpinion) {
        this.$message.warning('请输入审核意见')
        return
      }
      
      this.submitting = true
      try {
        // 提交审核前先保存安全生产评分到 scoreDetail（含 districtSafety 快照供市级端展示）
        await this.saveSafetyScore()
        const response = await submitAudit({
          evaluationId: this.evaluationInfo.id,
          action: parseInt(this.auditForm.finalResult),
          opinion: this.auditForm.finalOpinion
        })
        if (response.code === 200) {
          this.$message.success(this.auditForm.finalResult === '1' ? '审核通过成功' : '审核驳回成功')
          this.confirmDialogVisible = false
          this.goBack()
        } else {
          this.$message.error(response.message || '审核失败')
        }
      } catch (e) {
        console.error('审核失败', e)
        this.$message.error('审核失败')
      } finally {
        this.submitting = false
      }
    },

    // 构建安全生产评分 scoreDetail（合并到现有 scoreDetail，仅更新安全生产部分）
    buildSafetyScoreDetail() {
      const num = v => {
        const n = parseFloat(v)
        return isNaN(n) ? 0 : n
      }
      const scores = [
        num(this.auditForm.safetyScore1),
        num(this.auditForm.safetyScore2),
        num(this.auditForm.safetyScore3),
        num(this.auditForm.safetyScore4),
        num(this.auditForm.safetyScore5)
      ]
      // safetyResult: "1"=直接判D档, "2"=不直接判D档 → dGrade: "yes"/"no"
      const dGrade = this.auditForm.safetyResult === '1' ? 'yes' : 'no'
      // 保留现有 scoreDetail 其他字段，仅更新安全生产部分
      const existing = (this.evaluationInfo.scoreDetailMap && typeof this.evaluationInfo.scoreDetailMap === 'object')
        ? JSON.parse(JSON.stringify(this.evaluationInfo.scoreDetailMap))
        : {}
      // 区县端上传的安全生产文件（fileName + fileUrl）
      const files = this.safetyFiles.map(f => ({ fileName: f.fileName, fileUrl: f.fileUrl }))
      existing.safety = { scores, dGrade, files }
      // 区县端评分快照（区县审核时锁定，市级端只读展示）
      existing.districtSafety = { scores, dGrade, files }
      return JSON.stringify(existing)
    },

    // 保存安全生产评分
    async saveSafetyScore() {
      if (!this.evaluationInfo.id) return false
      try {
        const scoreDetail = this.buildSafetyScoreDetail()
        await saveEvaluationScore(this.evaluationInfo.id, scoreDetail)
        // 同步本地 scoreDetailMap，避免重复保存时丢失其他字段
        this.evaluationInfo.scoreDetailMap = JSON.parse(scoreDetail)
        return true
      } catch (e) {
        console.error('保存安全生产评分失败', e)
        this.$message.error('保存安全生产评分失败')
        return false
      }
    },

    // 查看审核记录
    async viewAuditRecords() {
      this.auditRecordsDialogVisible = true
      this.auditRecords = []
      if (!this.evaluationInfo.id) {
        return
      }
      try {
        const response = await getAuditHistory(this.evaluationInfo.id)
        if (response.code === 200 && response.data) {
          this.auditRecords = response.data
        } else {
          this.auditRecords = []
        }
      } catch (error) {
        console.error('获取审核记录失败', error)
        this.auditRecords = []
      }
    },

    // 保存
    async handleSave() {
      const ok = await this.saveSafetyScore()
      if (ok) {
        this.$message.success('保存成功')
      }
    },

    saveDraft() {
      this.handleSave()
    },

    goBack() {
      this.$router.push('/district/audit')
    }
  }
}
</script>

<style scoped>
.audit-detail {
  min-height: calc(100vh - 84px);
  background: #f5f7fa;
}

.page-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 12px 20px;
  background: #fff;
  border-bottom: 1px solid #eee;
}

.breadcrumb {
  display: flex;
  align-items: center;
  font-size: 14px;
  color: #606266;
}

.separator {
  margin: 0 8px;
  color: #ccc;
}

.header-right {
  display: flex;
  align-items: center;
  gap: 16px;
}

.user-info {
  font-size: 14px;
  color: #606266;
}

.main-content {
  display: flex;
  height: calc(100vh - 130px);
  min-width: 900px;
}

.left-sidebar {
  width: 200px;
  background: #fff;
  border-right: 1px solid #eee;
  padding: 12px 0;
}

.audit-menu {
  border-right: none;
}

.audit-menu .el-menu-item {
  height: 40px;
  line-height: 40px;
  padding-left: 20px;
  font-size: 13px;
  position: relative;
}

.audit-menu .el-menu-item.is-active {
  background: #e8f4fd;
  color: #1E40AF;
}

.audit-menu .el-menu-item.disabled-menu {
  color: #ccc;
  cursor: not-allowed;
}

.audit-menu .el-menu-item.disabled-menu:hover {
  background: transparent;
}

.audit-menu .el-menu-item.completed {
  color: #67c23a;
}

.menu-dot {
  display: inline-block;
  width: 8px;
  height: 8px;
  border-radius: 50%;
  background: #d9d9d9;
  margin-right: 8px;
  vertical-align: middle;
}

.menu-dot.completed-dot {
  background: #67c23a;
}

.step-number {
  display: inline-flex;
  align-items: center;
  justify-content: center;
  width: 20px;
  height: 20px;
  border-radius: 50%;
  background: #d9d9d9;
  color: #fff;
  font-size: 12px;
  font-weight: 500;
  margin-right: 8px;
  vertical-align: middle;
}

.step-number.completed {
  background: #67c23a;
}

.audit-menu .el-menu-item.is-active .step-number {
  background: #1E40AF;
}

.right-content {
  flex: 1;
  padding: 20px;
  overflow-y: auto;
  min-width: 0;
  box-sizing: border-box;
}

.tab-content {
  background: #fff;
  border-radius: 8px;
  padding: 24px;
}

.section-title-wrapper {
  margin-bottom: 20px;
}

.section-title {
  font-size: 16px;
  font-weight: 600;
  color: #303133;
}

.info-box {
  display: flex;
  align-items: flex-start;
  padding: 12px 16px;
  background: #f0f9ff;
  border-radius: 4px;
  margin-bottom: 16px;
}

.info-box i {
  color: #409EFF;
  margin-right: 10px;
  font-size: 14px;
}

.info-box span {
  flex: 1;
  font-size: 14px;
  color: #606266;
  line-height: 1.6;
}

.section-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 20px;
  padding-bottom: 16px;
  border-bottom: 1px solid #eee;
}

.section-header .section-title {
  font-size: 18px;
  font-weight: 600;
  color: #303133;
  position: relative;
  padding-left: 20px;
}

.section-header .section-title::before {
  content: '';
  position: absolute;
  left: 0;
  top: 50%;
  transform: translateY(-50%);
  width: 4px;
  height: 18px;
  background: #409EFF;
}

.tip-box {
  padding: 12px 16px;
  border-radius: 4px;
  margin-bottom: 16px;
  font-size: 14px;
  color: #606266;
  line-height: 1.6;
}

.tip-box.success {
  background: #f0f9ff;
  border-left: 4px solid #67c23a;
}

.tip-box.success i {
  color: #67c23a;
  margin-right: 8px;
}

.tip-box.info {
  background: #fafafa;
  border: 1px solid #e4e7ed;
}

.tip-box.info.highlight {
  background: #fff3cd;
  border-color: #ffeeba;
  font-weight: 500;
}

.tip-box span {
  display: block;
}

.score-input-wrapper {
  margin-top: 10px;
  display: flex;
  align-items: center;
  gap: 8px;
}

.score-label {
  font-size: 14px;
  color: #606266;
}

.safety-score-input {
  width: 120px;
}

.form-section {
  margin-bottom: 24px;
}

.form-label {
  font-size: 14px;
  color: #606266;
  line-height: 1.8;
  display: block;
  margin-bottom: 12px;
}

.radio-group {
  display: flex;
  gap: 24px;
}

.radio-group .el-radio {
  margin-right: 0;
}

.file-list {
  display: flex;
  flex-direction: column;
  gap: 8px;
}

.file-row {
  margin-top: 8px;
}

.file-item {
  display: flex;
  align-items: center;
  padding: 10px 12px;
  background: #fafafa;
  border-radius: 4px;
  gap: 10px;
}

.file-item-row {
  display: flex;
  align-items: center;
  padding: 10px 12px;
  background: #fafafa;
  border-radius: 4px;
  gap: 12px;
}

.file-item i,
.file-item-row i {
  color: #909399;
  font-size: 16px;
}

.file-item span,
.file-item-row span {
  flex: 1;
  font-size: 14px;
  color: #606266;
}

.file-text {
  font-size: 14px;
  color: #606266;
  margin-right: 12px;
}

.file-action {
  color: #409EFF;
  font-size: 14px;
  text-decoration: none;
}

.file-action:hover {
  text-decoration: underline;
}

.score-input {
  width: 200px;
}

.score-input-small {
  width: 150px;
}

.opinion-input {
  width: 100%;
}

.checkbox-section {
  padding-top: 16px;
  border-top: 1px solid #eee;
}

.table-wrapper {
  margin: 16px 0;
}

.table-wrapper .el-table {
  font-size: 13px;
}

.reason-select {
  width: 200px;
}

.bottom-actions {
  display: flex;
  justify-content: flex-end;
  gap: 12px;
  margin-top: 30px;
  padding-top: 20px;
  border-top: 1px solid #eee;
}

.dialog-footer {
  display: flex;
  justify-content: flex-end;
  gap: 12px;
}

/* 二次确认对话框样式 */
.confirm-content {
  display: flex;
  align-items: center;
  padding: 20px 0;
}

.confirm-icon {
  margin-right: 12px;
}

.confirm-icon i {
  font-size: 28px;
  color: #67c23a;
}

.confirm-text {
  font-size: 14px;
  color: #303133;
  line-height: 1.5;
}

.file-section {
  margin-bottom: 16px;
}

.file-title {
  font-size: 14px;
  font-weight: 500;
  color: #303133;
  margin-bottom: 8px;
}

.file-hint {
  font-weight: normal;
  color: #909399;
  margin-left: 8px;
}

.upload-btn {
  color: #409EFF;
  font-size: 14px;
  padding: 0;
}

.upload-btn:hover {
  color: #67c23a;
}

.upload-btn-wrapper {
  margin-top: 12px;
}

/* 行文文件上传区样式 */
.document-section {
  background: #f5f7fa;
  border: 1px solid #e4e7ed;
  border-radius: 8px;
  padding: 16px;
  margin: 16px 0;
}

.document-title {
  font-size: 14px;
  font-weight: 500;
  color: #303133;
  margin-bottom: 12px;
}

.document-hint {
  font-weight: normal;
  font-size: 12px;
  color: #909399;
  margin-left: 4px;
}

.document-item {
  display: flex;
  align-items: center;
  gap: 8px;
  padding: 8px 12px;
  background: #fff;
  border-radius: 4px;
  margin-bottom: 12px;
  border: 1px solid #dcdfe6;
}

.document-item i {
  color: #409eff;
  font-size: 16px;
}

.document-name {
  flex: 1;
  font-size: 13px;
  color: #303133;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}

.document-empty {
  font-size: 13px;
  color: #909399;
  padding: 8px 12px;
  margin-bottom: 12px;
}

.file-input {
  display: none;
}

.audit-conclusion {
  background: #fafafa;
  border: 1px solid #e4e7ed;
  border-radius: 4px;
  padding: 16px;
  margin-top: 16px;
}

.conclusion-title {
  font-size: 14px;
  font-weight: 500;
  color: #303133;
  margin-bottom: 12px;
}

.confirm-btn {
  margin-top: 12px;
}

/* 文件预览对话框样式 */
.preview-dialog {
  .el-dialog__header {
    padding: 16px 20px;
    border-bottom: 1px solid #eee;
  }
  
  .el-dialog__title {
    font-size: 14px;
    font-weight: 500;
    color: #303133;
  }
  
  .el-dialog__body {
    padding: 20px;
    max-height: 500px;
    overflow-y: auto;
  }
}

.preview-content {
  background: #fff;
  border-radius: 4px;
}

.preview-document {
  padding: 20px;
}

.document-title {
  font-size: 16px;
  font-weight: 600;
  color: #303133;
  text-align: center;
  margin-bottom: 24px;
}

.document-content {
  font-size: 14px;
  color: #606266;
  line-height: 1.8;
  text-indent: 2em;
  margin-bottom: 12px;
}

.preview-footer {
  display: flex;
  justify-content: flex-end;
  gap: 12px;
  padding: 16px 20px;
  border-top: 1px solid #eee;
  margin-top: -10px;
}

/* 文件上传区域样式 */
.file-upload-area {
  margin-bottom: 12px;
}

.file-upload-area .upload-btn-wrapper {
  display: inline-flex;
  align-items: center;
  gap: 6px;
  padding: 6px 12px;
  border: 1px dashed #d9d9d9;
  border-radius: 4px;
  color: #409EFF;
  font-size: 14px;
  cursor: pointer;
  transition: all 0.3s;
}

.file-upload-area .upload-btn-wrapper:hover {
  border-color: #409EFF;
  background: #e8f4fd;
}

.file-upload-area .upload-btn-wrapper i {
  font-size: 14px;
}

.hidden-file-input {
  display: none;
}

.empty-file-tip {
  font-size: 13px;
  color: #909399;
  padding: 12px;
  background: #fafafa;
  border-radius: 4px;
  text-align: center;
}

.tech-file-item {
  align-items: flex-start;
}
.tech-file-item .el-icon-file-text {
  margin-top: 2px;
}
.tech-file-info {
  flex: 1;
  display: flex;
  flex-direction: column;
  gap: 4px;
}
.tech-file-name {
  font-size: 14px;
  color: #303133;
}
.tech-file-meta {
  font-size: 12px;
  color: #909399;
}
.meta-sep {
  margin: 0 4px;
  color: #dcdfe6;
}

.file-list {
  display: flex;
  flex-direction: column;
  gap: 8px;
}

.file-item {
  display: flex;
  align-items: center;
  padding: 10px 12px;
  background: #fafafa;
  border-radius: 4px;
}

.file-item i {
  color: #409EFF;
  font-size: 16px;
  margin-right: 10px;
}

.file-item .file-name {
  flex: 1;
  font-size: 14px;
  color: #303133;
}

.file-item .file-size {
  font-size: 13px;
  color: #909399;
  margin-right: 16px;
}

.file-item .file-action {
  font-size: 14px;
  margin-right: 16px;
  cursor: pointer;
}

.file-item .file-action.preview {
  color: #409EFF;
}

.file-item .file-action.delete {
  color: #f56c6c;
  margin-right: 0;
}

.file-item .file-action:hover {
  text-decoration: underline;
}

/* 顶部操作栏 */
.top-bar {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 16px 20px;
  background: #fff;
  border-bottom: 1px solid #eee;
}

.top-bar-left {
  flex: 1;
}

.page-title {
  font-size: 16px;
  font-weight: 600;
  color: #303133;
  margin: 0;
}

.top-bar-right {
  display: flex;
  gap: 12px;
}

/* 时间线样式 */
.timeline-container {
  max-height: 500px;
  overflow-y: auto;
  padding-left: 24px;
  position: relative;
}

.timeline-container::before {
  content: '';
  position: absolute;
  left: 7px;
  top: 0;
  bottom: 0;
  width: 2px;
  background: #e8e8e8;
}

.timeline-item {
  position: relative;
  margin-bottom: 24px;
}

.timeline-dot {
  width: 16px;
  height: 16px;
  border-radius: 50%;
  background: #d9d9d9;
  border: 3px solid #fff;
  box-shadow: 0 0 0 2px #d9d9d9;
  position: absolute;
  left: -17px;
  top: 4px;
  z-index: 1;
}

.timeline-dot.success {
  background: #67c23a;
  box-shadow: 0 0 0 2px #b3e19d;
}

.timeline-dot.default {
  background: #d9d9d9;
  box-shadow: 0 0 0 2px #e8e8e8;
}

.timeline-dot.warning {
  background: #f56c6c;
  box-shadow: 0 0 0 2px #fbc4c4;
}

.timeline-card {
  background: #fff;
  border-radius: 8px;
  padding: 16px;
  border: 1px solid #e8e8e8;
  transition: all 0.3s;
  margin-top: 0;
}

.record-time {
  font-size: 12px;
  color: #909399;
  margin-bottom: 8px;
}

.record-content {
  font-size: 14px;
  color: #303133;
  line-height: 1.6;
}

.empty-tip {
  text-align: center;
  color: #909399;
  padding: 40px 0;
  font-size: 14px;
}

.record-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 12px;
}

.park-name {
  font-size: 14px;
  font-weight: 500;
  color: #303133;
  display: flex;
  align-items: center;
  gap: 6px;
}

.park-name i {
  color: #409eff;
}

.status-tag {
  font-size: 12px;
  padding: 2px 8px;
  background: #e8e8e8 !important;
  color: #606266 !important;
  border: none;
}

.status-change {
  display: flex;
  align-items: center;
  gap: 8px;
  font-size: 13px;
  color: #606266;
  margin-bottom: 12px;
  padding: 6px 10px;
  background: #f9f9f9;
  border-radius: 4px;
}

.status-change i {
  color: #c0c4cc;
  font-size: 14px;
}

.auditor {
  display: flex;
  align-items: center;
  gap: 4px;
  font-size: 12px;
  color: #909399;
  padding: 6px 10px;
  background: #f5f5f5;
  border-radius: 4px;
  width: fit-content;
  margin-bottom: 12px;
}

.auditor i {
  font-size: 12px;
}

.audit-opinion {
  background: #f9f9f9;
  border-radius: 4px;
  padding: 12px;
  margin-top: 8px;
  border-left: 3px solid #409eff;
}

.opinion-label {
  display: flex;
  align-items: center;
  gap: 4px;
  font-size: 12px;
  color: #909399;
  margin-bottom: 6px;
}

.opinion-label i {
  color: #409eff;
}

.opinion-content {
  font-size: 13px;
  color: #606266;
  line-height: 1.6;
}
</style>