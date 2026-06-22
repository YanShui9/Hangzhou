<template>
  <div class="audit-detail">
    <!-- 顶部操作栏 -->
    <div class="page-header">
      <el-button @click="goBack" icon="el-icon-arrow-left">返回列表</el-button>
      <span class="title">{{ evaluationInfo.parkName || '园区评价审核' }}</span>
      <div class="header-actions">
        <el-button type="primary" icon="el-icon-check" @click="handleAudit(1)" v-if="canAudit">通过</el-button>
        <el-button type="danger" icon="el-icon-close" @click="handleAudit(2)" v-if="canAudit">驳回</el-button>
        <el-button icon="el-icon-save" @click="saveDraft">保存</el-button>
        <el-button type="primary" @click="showAuditRecord">审核记录</el-button>
      </div>
    </div>

    <div class="main-content">
      <!-- 左侧导航 -->
      <div class="left-sidebar">
        <el-menu :default-active="activeIndex" mode="vertical" class="audit-menu">
          <el-menu-item index="1" @click="switchTab(1)">
            <span>1. 基础指标</span>
          </el-menu-item>
          <el-menu-item index="2" @click="switchTab(2)">
            <span>2. 产业发展</span>
          </el-menu-item>
          <el-menu-item index="3" @click="switchTab(3)">
            <span>3. 企业培育</span>
          </el-menu-item>
          <el-menu-item index="4" @click="switchTab(4)">
            <span>4. 科技创新</span>
          </el-menu-item>
          <el-menu-item index="5" @click="switchTab(5)">
            <span>5. 服务能力</span>
          </el-menu-item>
          <el-menu-item index="6" @click="switchTab(6)">
            <span>6. 效益产出</span>
          </el-menu-item>
          <el-menu-item index="7" @click="switchTab(7)">
            <span>7. 安全生产</span>
          </el-menu-item>
          <el-menu-item index="8" @click="switchTab(8)">
            <span>8. 其他</span>
          </el-menu-item>
          <el-menu-item index="9" @click="switchTab(9)">
            <span>9. 审核结果</span>
          </el-menu-item>
        </el-menu>
      </div>

      <!-- 右侧内容区 -->
      <div class="right-content">
        <!-- 基础指标 -->
        <div v-show="activeIndex === '1'" class="tab-content">
          <div class="section-header">
            <h3 class="section-title">1. 基础指标</h3>
          </div>
          <el-card shadow="never" class="info-card">
            <div class="form-item">
              <div class="info-box">
                <i class="el-icon-info"></i>
                <span>评价年度内参评园区需符合《杭州市升级版小微企业园区建设和管理工作指引（试行）》明确的小微企业园认定条件，不具备的直接判D档。</span>
              </div>
            </div>
            <div class="form-item">
              <label class="form-label">审核意见：</label>
              <div class="radio-group">
                <el-radio v-model="auditForm.basicResult" label="1">通过</el-radio>
                <el-radio v-model="auditForm.basicResult" label="2">驳回</el-radio>
                <el-radio v-model="auditForm.basicResult" label="3">暂缓</el-radio>
                <el-radio v-model="auditForm.basicResult" label="4">退出</el-radio>
              </div>
            </div>
            <div class="form-item">
              <label class="form-label">2025年高新技术企业名单</label>
              <div class="file-list">
                <div class="file-item">
                  <el-icon name="el-icon-file-text"></el-icon>
                  <span>文件名文件名文件名文件名文件名文件名文件名.xlsx</span>
                  <a href="#" class="file-action">预览</a>
                </div>
              </div>
            </div>
            <div class="form-item checkbox-item">
              <el-checkbox v-model="auditForm.basicConfirm">我已知晓</el-checkbox>
            </div>
          </el-card>
          <div class="bottom-actions">
            <el-button type="primary" @click="nextStep">下一步</el-button>
          </div>
        </div>

        <!-- 产业发展 -->
        <div v-show="activeIndex === '2'" class="tab-content">
          <div class="section-header">
            <h3 class="section-title">2. 产业发展</h3>
          </div>
          <el-card shadow="never" class="info-card">
            <div class="form-item">
              <label class="form-label">①园区产业围绕五大产业生态圈（智能物联、生物医药、高端装备、新材料和绿色能源五大产业生态圈）集聚发展，并以此形成主导产业的，得5分。</label>
            </div>
            <div class="form-item">
              <label class="form-label">②园区主导产业明确，效益突出，评价年度内主导产业产值占（营收）及入驻企业数占比均超过50%，两项占比在50%的基础上每项增加10%，得10分。</label>
            </div>
            <div class="form-item">
              <label class="form-label">③评价年度内园区生产型企业不少于10家，产值占比不低于60%，产值占比在60%的基础上每增加10%，得10分。</label>
            </div>
            <div class="form-item">
              <label class="form-label">④评价年度内园区注册企业列表名单。</label>
            </div>
            <div class="table-wrapper">
              <el-table :data="enterpriseList" border style="width: 100%;" max-height="400px">
                <el-table-column prop="rowIndex" label="序号" width="60" align="center" />
                <el-table-column prop="parkName" label="园区名称" min-width="150" />
                <el-table-column prop="enterpriseName" label="入驻企业名称" min-width="200" />
                <el-table-column prop="creditCode" label="统一社会信用代码" min-width="180" />
                <el-table-column prop="registerDate" label="入驻起止时间" min-width="150" />
                <el-table-column prop="address" label="企业注册地址" min-width="200" />
              </el-table>
            </div>
          </el-card>
          <div class="bottom-actions">
            <el-button @click="prevStep">上一步</el-button>
            <el-button type="primary" @click="nextStep">下一步</el-button>
          </div>
        </div>

        <!-- 企业培育 -->
        <div v-show="activeIndex === '3'" class="tab-content">
          <div class="section-header">
            <h3 class="section-title">3. 企业培育</h3>
          </div>
          <el-card shadow="never" class="info-card">
            <div class="form-item">
              <label class="form-label">①园区内集聚的规模以上工业企业每增1得1分；评价年度内园区每新增1家规模以上工业企业得2分；评价年度内有规模以上工业企业退规下的，每退减1家减1分。</label>
            </div>
            <div class="form-item">
              <label class="form-label">②评价年度内园区内企业每新增一家国家级制造业单项冠军得4分；每新增一家上市企业得3分；每新增一家国家级专精特新"小巨人"企业或省级高新技术企业得1分；每新增一家创新型中小企业得0.5分，同一企业不重复计算。</label>
            </div>
            <div class="form-item">
              <label class="form-label">③园区建立专精特新企业库，评价年度内园区内企业"提早转小设（创）"案例，每新增1个得1分。</label>
            </div>
            <div class="form-item">
              <label class="form-label">2025年高新技术企业名单</label>
              <div class="file-row">
                <div class="file-item">
                  <el-icon name="el-icon-file-text"></el-icon>
                  <span>文件名文件名文件名文件名文件名文件名文件名.xlsx</span>
                  <a href="#" class="file-action">预览</a>
                  <el-input v-model="auditForm.enterpriseScore" type="number" placeholder="请输入得分（满分55分）" class="score-input" />
                </div>
              </div>
              <el-input v-model="auditForm.enterpriseOpinion" type="textarea" :rows="4" placeholder="请输入审核意见（满分55分）" class="opinion-input" />
            </div>
          </el-card>
          <div class="bottom-actions">
            <el-button @click="prevStep">上一步</el-button>
            <el-button type="primary" @click="nextStep">下一步</el-button>
          </div>
        </div>

        <!-- 科技创新 -->
        <div v-show="activeIndex === '4'" class="tab-content">
          <div class="section-header">
            <h3 class="section-title">4. 科技创新</h3>
          </div>
          <el-card shadow="never" class="info-card">
            <div class="form-item">
              <label class="form-label">①评价年度内园区企业新增省级及以上研发机构（重点、省重点、省技术、省级优秀工业新产品、浙江制造精品的，每项得2分。</label>
            </div>
            <div class="form-item">
              <label class="form-label">②评价年度内园区企业新增国家、省、市级企业研发机构的，每项分别得5分、2分、1分，同一企业机构不重复计算。</label>
            </div>
            <div class="form-item">
              <label class="form-label">③评价年度内园区企业引进独立注册的市级及以上科创创新、企业孵化器和科技创业公共服务平台等的，每项得2分，同一项目不重复计算。</label>
            </div>
            <div class="form-item">
              <label class="form-label">④根据杭州市高新技术企业分类目录标准，评价年度内园区企业认定A、B、C、D类人才的，每人分别得5分、2分、1.5分、1分。</label>
            </div>
            <div class="table-wrapper">
              <el-table :data="talentList" border style="width: 100%;" max-height="300px">
                <el-table-column prop="level" label="类别" width="80" />
                <el-table-column prop="name" label="姓名" width="100" />
                <el-table-column prop="company" label="所属企业" min-width="150" />
                <el-table-column prop="certDate" label="认定日期" width="120" />
                <el-table-column prop="file" label="附件" width="200">
                  <template slot-scope="scope">
                    <a href="#" class="file-link">文件名文件名文件名文件名...</a>
                  </template>
                </el-table-column>
                <el-table-column prop="score" label="得分" width="120" align="center">
                  <template slot-scope="scope">
                    <el-input v-model="scope.row.score" type="number" :min="0" :max="5" style="width: 80px;" />
                  </template>
                </el-table-column>
              </el-table>
            </div>
            <div class="form-item">
              <label class="form-label">意见</label>
              <el-input v-model="auditForm.techOpinion" type="textarea" :rows="3" placeholder="请输入审核意见" class="opinion-input" />
            </div>
            <div class="form-item">
              <label class="form-label">⑤与园区与科研院所建立合作关系，在园区开展科技成果转化并在评价年度形成500万元以上产出的，每项得15分。</label>
            </div>
            <div class="form-item">
              <label class="form-label">2025年高新技术企业名单</label>
              <div class="file-row">
                <div class="file-item">
                  <el-icon name="el-icon-file-text"></el-icon>
                  <span>文件名文件名文件名文件名文件名文件名文件名.xlsx</span>
                  <a href="#" class="file-action">预览</a>
                  <el-input v-model="auditForm.techScore" type="number" placeholder="请输入得分（满分55分）" class="score-input" />
                </div>
              </div>
              <el-input v-model="auditForm.techOpinion2" type="textarea" :rows="3" placeholder="请输入审核意见（满分55分）" class="opinion-input" />
            </div>
          </el-card>
          <div class="bottom-actions">
            <el-button @click="prevStep">上一步</el-button>
            <el-button type="primary" @click="nextStep">下一步</el-button>
          </div>
        </div>

        <!-- 服务能力 -->
        <div v-show="activeIndex === '5'" class="tab-content">
          <div class="section-header">
            <h3 class="section-title">5. 服务能力</h3>
          </div>
          <el-card shadow="never" class="info-card">
            <div class="form-item">
              <label class="form-label">①园区建立企业服务站（工作室）并有明确完善的企业服务机制的，得5分；对入园企业项目报批实行一站式全程代理代办服务的，得5分；按规定组织企业开展党、团建及工会活动的，得5分。</label>
            </div>
            <div class="form-item">
              <label class="form-label">物业服务站建设材料</label>
              <div class="file-row">
                <div class="file-item">
                  <el-icon name="el-icon-file-text"></el-icon>
                  <span>文件名文件名文件名文件名文件名文件名文件名</span>
                  <a href="#" class="file-action">预览</a>
                  <el-input v-model="auditForm.serviceScore1" type="number" placeholder="请输入得分（满分15分）" class="score-input" />
                </div>
              </div>
            </div>
            <div class="form-item">
              <label class="form-label">一站式代办服务材料</label>
              <div class="file-row">
                <div class="file-item">
                  <el-icon name="el-icon-file-text"></el-icon>
                  <span>文件名文件名文件名文件名文件名文件名文件名</span>
                  <a href="#" class="file-action">预览</a>
                  <el-input v-model="auditForm.serviceScore2" type="number" placeholder="请输入得分（满分15分）" class="score-input" />
                </div>
              </div>
            </div>
            <div class="form-item">
              <label class="form-label">党团工活动材料</label>
              <div class="file-row">
                <div class="file-item">
                  <el-icon name="el-icon-file-text"></el-icon>
                  <span>文件名文件名文件名文件名文件名文件名文件名</span>
                  <a href="#" class="file-action">预览</a>
                  <el-input v-model="auditForm.serviceScore3" type="number" placeholder="请输入得分（满分15分）" class="score-input" />
                </div>
              </div>
              <el-input v-model="auditForm.serviceOpinion" type="textarea" :rows="4" placeholder="请输入审核意见" class="opinion-input" />
            </div>
            <div class="form-item">
              <label class="form-label">②建立园区大脑，并获评省级数字化示范园区的，得5分；未获得省级数字化示范园区的，能集聚园区数据资源，集成相关功能模块，利用实时数据优化园区公共资源，实现园区运营智能化，助力园区运营提升的，得4分；未建立园区大脑的最高得3分。</label>
            </div>
            <div class="form-item">
              <label class="form-label">园区大脑数字化建设材料</label>
              <div class="file-row">
                <div class="file-item">
                  <el-icon name="el-icon-file-text"></el-icon>
                  <span>文件名文件名文件名文件名文件名文件名文件名</span>
                  <a href="#" class="file-action">预览</a>
                  <el-input v-model="auditForm.digitalScore" type="number" placeholder="请输入得分（满分5分）" class="score-input" />
                </div>
              </div>
              <el-input v-model="auditForm.digitalOpinion" type="textarea" :rows="4" placeholder="请输入审核意见" class="opinion-input" />
            </div>
            <div class="form-item">
              <label class="form-label">③评价年度内开展普惠性服务活动10次以上/每类活动参加企业50家以上得1分；开展针对性个性化服务活动达20次以上的，得2分。</label>
            </div>
            <div class="form-item">
              <label class="form-label">普惠性服务活动</label>
              <div class="file-row">
                <div class="file-item">
                  <el-icon name="el-icon-file-text"></el-icon>
                  <span>文件名文件名文件名文件名文件名文件名文件名</span>
                  <a href="#" class="file-action">预览</a>
                  <el-input v-model="auditForm.publicScore" type="number" placeholder="请输入得分（满分15分）" class="score-input" />
                </div>
              </div>
            </div>
            <div class="form-item">
              <label class="form-label">个性化服务活动</label>
              <div class="file-row">
                <div class="file-item">
                  <el-icon name="el-icon-file-text"></el-icon>
                  <span>文件名文件名文件名文件名文件名文件名文件名</span>
                  <a href="#" class="file-action">预览</a>
                  <el-input v-model="auditForm.personalScore" type="number" placeholder="请输入得分（满分2分）" class="score-input" />
                </div>
              </div>
              <el-input v-model="auditForm.activityOpinion" type="textarea" :rows="4" placeholder="请输入审核意见" class="opinion-input" />
            </div>
            <div class="form-item">
              <label class="form-label">④园区与杭州市范围内的其他小微企业园区形成合作关系，进行资源服务输出，评价年度实现产业、科研合作3个以上项目的，得3分。</label>
            </div>
            <div class="form-item">
              <label class="form-label">项目名称</label>
              <div class="file-row">
                <div class="file-item">
                  <el-icon name="el-icon-file-text"></el-icon>
                  <span>文件名文件名文件名文件名文件名文件名文件名</span>
                  <a href="#" class="file-action">预览</a>
                  <el-input v-model="auditForm.cooperationScore" type="number" placeholder="请输入得分（满分3分）" class="score-input" />
                </div>
              </div>
              <el-input v-model="auditForm.cooperationOpinion" type="textarea" :rows="4" placeholder="请输入审核意见" class="opinion-input" />
            </div>
          </el-card>
          <div class="bottom-actions">
            <el-button @click="prevStep">上一步</el-button>
            <el-button type="primary" @click="nextStep">下一步</el-button>
          </div>
        </div>

        <!-- 效益产出 -->
        <div v-show="activeIndex === '6'" class="tab-content">
          <div class="section-header">
            <h3 class="section-title">6. 效益产出</h3>
          </div>
          <el-card shadow="never" class="info-card">
            <div class="form-item">
              <label class="form-label">①评价年度亩均税收达到全省制造业类园区平均水平：1.5-2倍得2分；2-2.5倍得4分；2.5-3倍得6分；3-3.5倍得8分；3.5倍以上得10分。</label>
            </div>
            <div class="form-item">
              <label class="form-label">②评价年度亩均产出达到全省制造业类园区平均水平：1.5-2倍得2分；2-2.5倍得4分；2.5-3倍得6分；3.5倍及以上得10分。</label>
            </div>
            <div class="form-item">
              <label class="form-label">③贯彻落实集约发展理念，通过改造提升实现工业上楼、效益提升的，得5分。</label>
            </div>
            <div class="form-item">
              <label class="form-label">项目名称</label>
              <div class="file-row">
                <div class="file-item">
                  <el-icon name="el-icon-file-text"></el-icon>
                  <span>文件名文件名文件名文件名文件名文件名文件名</span>
                  <a href="#" class="file-action">预览</a>
                  <el-input v-model="auditForm.outputScore" type="number" placeholder="请输入得分" class="score-input" />
                </div>
              </div>
              <el-input v-model="auditForm.outputOpinion" type="textarea" :rows="5" placeholder="请输入审核意见" class="opinion-input" />
            </div>
          </el-card>
          <div class="bottom-actions">
            <el-button @click="prevStep">上一步</el-button>
            <el-button type="primary" @click="nextStep">下一步</el-button>
          </div>
        </div>

        <!-- 安全生产 -->
        <div v-show="activeIndex === '7'" class="tab-content">
          <div class="section-header">
            <h3 class="section-title">7. 安全生产</h3>
          </div>
          <el-card shadow="never" class="info-card">
            <div class="form-item">
              <label class="form-label">①未落实《杭州市小微企业园区安全管理通则》要求，经查实的，扣2分；</label>
              <el-input v-model="auditForm.safetyScore1" type="number" placeholder="请输入得分" class="score-input" />
            </div>
            <div class="form-item">
              <label class="form-label">②未签订消防安全责任书的，扣2分；未落实培训、演练要求的，扣2分；</label>
              <el-input v-model="auditForm.safetyScore2" type="number" placeholder="请输入得分" class="score-input" />
            </div>
            <div class="form-item">
              <label class="form-label">③消防设施器材不完整或过期的，扣2分；</label>
              <el-input v-model="auditForm.safetyScore3" type="number" placeholder="请输入得分" class="score-input" />
            </div>
            <div class="form-item">
              <label class="form-label">④存在安全隐患被省、市主管部门通报的，每次扣2分。</label>
              <el-input v-model="auditForm.safetyScore4" type="number" placeholder="请输入得分" class="score-input" />
            </div>
            <div class="form-item">
              <label class="form-label">⑤近一年内发生较大以上的安全生产事故或较大影响的社会事件，园区安全生产隐患未按期整改的，直接判D档。</label>
              <div class="radio-group">
                <el-radio v-model="auditForm.safetyResult" label="1">列入D档</el-radio>
                <el-radio v-model="auditForm.safetyResult" label="2">不列入D档</el-radio>
              </div>
            </div>
          </el-card>
          <div class="bottom-actions">
            <el-button @click="prevStep">上一步</el-button>
            <el-button type="primary" @click="nextStep">下一步</el-button>
          </div>
        </div>

        <!-- 其他 -->
        <div v-show="activeIndex === '8'" class="tab-content">
          <div class="section-header">
            <h3 class="section-title">8. 其他</h3>
          </div>
          <el-card shadow="never" class="info-card">
            <div class="form-item">
              <label class="form-label">①园区在浙江省小微企业园企业信息管理系统中未按时完整报送小微企业园季度运行情况，少报一次减2分；年度数据未按期直接判D档。</label>
            </div>
            <div class="form-item">
              <label class="form-label">②申报材料、报送数据作伪的，运营管理机构被列入社会公共信用信息平台失信联合惩戒对象名单的，直接判D档。</label>
              <div class="radio-group">
                <el-radio v-model="auditForm.otherResult" label="1">列入D档</el-radio>
                <el-radio v-model="auditForm.otherResult" label="2">不列入D档</el-radio>
              </div>
            </div>
            <div class="form-item">
              <label class="form-label">③评价年度内有媒体负面报道，经查实并造成较大影响的，扣6分。</label>
              <el-input v-model="auditForm.mediaScore" type="number" placeholder="请输入得分" class="score-input" />
            </div>
            <div class="form-item">
              <label class="form-label">本年度评价承诺函：</label>
              <div class="file-row">
                <div class="file-item">
                  <el-icon name="el-icon-file-text"></el-icon>
                  <span>承诺函</span>
                  <a href="#" class="file-action">预览</a>
                </div>
              </div>
            </div>
            <div class="form-item">
              <label class="form-label">承诺函</label>
              <div class="file-row">
                <div class="file-item">
                  <el-icon name="el-icon-file-text"></el-icon>
                  <span>文件名文件名文件名文件名文件名文件名文件名</span>
                  <a href="#" class="file-action">预览</a>
                </div>
              </div>
            </div>
          </el-card>
          <div class="bottom-actions">
            <el-button @click="prevStep">上一步</el-button>
            <el-button type="primary" @click="nextStep">下一步</el-button>
          </div>
        </div>

        <!-- 审核结果 -->
        <div v-show="activeIndex === '9'" class="tab-content">
          <div class="section-header">
            <h3 class="section-title">9. 审核结果</h3>
          </div>
          <el-card shadow="never" class="info-card">
            <div class="form-item">
              <label class="form-label">审核结果：</label>
              <div class="radio-group">
                <el-radio v-model="auditForm.finalResult" label="1">通过</el-radio>
                <el-radio v-model="auditForm.finalResult" label="2">驳回</el-radio>
              </div>
            </div>
            <div class="form-item">
              <label class="form-label">驳回指标：</label>
              <div class="radio-group">
                <el-radio v-model="auditForm.rejectReason" label="1">通过</el-radio>
                <el-radio v-model="auditForm.rejectReason" label="2">驳回</el-radio>
              </div>
            </div>
            <div class="form-item">
              <label class="form-label">绩效分档：</label>
              <el-select v-model="auditForm.grade" placeholder="请选择绩效分档" class="grade-select">
                <el-option label="A" value="A" />
                <el-option label="B" value="B" />
                <el-option label="C" value="C" />
                <el-option label="D" value="D" />
              </el-select>
            </div>
            <div class="form-item">
              <label class="form-label">审核意见：</label>
              <el-input v-model="auditForm.finalOpinion" type="textarea" :rows="6" placeholder="请输入审核意见" class="opinion-input" />
            </div>
          </el-card>
          <div class="bottom-actions">
            <el-button @click="prevStep">上一步</el-button>
            <el-button type="primary" @click="confirmSubmit">确认</el-button>
          </div>
        </div>
      </div>
    </div>

    <!-- 审核操作对话框 -->
    <el-dialog :title="auditDialogTitle" :visible.sync="auditDialogVisible" width="500px" :close-on-click-modal="false">
      <el-form :model="auditForm" label-width="100px">
        <el-form-item label="审核结果">
          <el-tag :type="auditForm.action === 1 ? 'success' : 'danger'" size="large">
            {{ auditForm.action === 1 ? '通过' : '驳回' }}
          </el-tag>
        </el-form-item>
        <el-form-item label="审核意见">
          <el-input
            v-model="auditForm.finalOpinion"
            type="textarea"
            :rows="5"
            placeholder="请输入审核意见"
          />
        </el-form-item>
      </el-form>
      <div slot="footer" class="dialog-footer">
        <el-button @click="auditDialogVisible = false">取消</el-button>
        <el-button type="primary" :loading="submitting" @click="confirmAudit">确认{{ auditForm.action === 1 ? '通过' : '驳回' }}</el-button>
      </div>
    </el-dialog>

    <!-- 审核记录对话框 -->
    <el-dialog title="审核记录" :visible.sync="showRecordDialog" width="600px">
      <el-table :data="auditRecords" border style="width: 100%;">
        <el-table-column prop="auditTime" label="审核时间" width="150" />
        <el-table-column prop="auditor" label="审核人" width="100" />
        <el-table-column prop="auditLevel" label="审核层级" width="100" />
        <el-table-column prop="result" label="审核结果" width="100" />
        <el-table-column prop="opinion" label="审核意见" />
      </el-table>
      <div slot="footer" class="dialog-footer">
        <el-button @click="showRecordDialog = false">关闭</el-button>
      </div>
    </el-dialog>
  </div>
</template>

<script>
import { getEvaluationDetail, submitAudit } from '@/api/audit'

export default {
  name: 'AuditDetail',
  data() {
    return {
      activeIndex: '1',
      evaluationInfo: {},
      canAudit: true,
      submitting: false,
      auditDialogVisible: false,
      auditDialogTitle: '',
      showRecordDialog: false,
      stepCompleted: {
        '1': false,
        '2': false,
        '3': false,
        '4': false,
        '5': false,
        '6': false,
        '7': false,
        '8': false,
        '9': false
      },
      auditForm: {
        evaluationId: null,
        action: null,
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
        outputScore: '',
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
        grade: '',
        finalOpinion: ''
      },
      enterpriseList: [
        { rowIndex: 1, parkName: '万轮科技园', enterpriseName: '杭州怡明医疗器械股份有限公司', creditCode: '913301001695775M', registerDate: '2022-06-30至', address: '浙江省杭州市滨江区江南大道699号' },
        { rowIndex: 2, parkName: '传化科创园', enterpriseName: '杭州艾名医学科技有限公司', creditCode: '91330108MA2JG5G55D', registerDate: '2022-06-30至', address: '浙江省杭州市滨江区西兴街道江陵路88号3幢' },
        { rowIndex: 3, parkName: '和达药谷中心', enterpriseName: '杭州环特生物科技股份有限公司', creditCode: '9133010556612982F', registerDate: '2022-06-30至', address: '浙江省杭州市江干区江潮路88号杭州医药港9号楼' },
        { rowIndex: 4, parkName: '颐高创业园', enterpriseName: '杭州禾睿康宇医药科技科技有限公司', creditCode: '91330108MA2JG5GL9G', registerDate: '2022-06-30至', address: '浙江省杭州市滨江区西兴街道江陵路88号' },
        { rowIndex: 5, parkName: '天和国际产业园', enterpriseName: '杭州腾品科技有限公司', creditCode: '9133010856673312C', registerDate: '2022-06-30至', address: '浙江省杭州市滨江区江南大道88号万轮科技园' },
        { rowIndex: 6, parkName: '乐富海邦园', enterpriseName: '杭州启明医疗器械股份有限公司', creditCode: '91330108MA2JG5G55D', registerDate: '2022-06-30至', address: '浙江省杭州市滨江区江南大道699号' },
        { rowIndex: 7, parkName: '银海科创中心', enterpriseName: '杭州艾名医学科技有限公司', creditCode: '91330108MA2JG5G55D', registerDate: '2022-06-30至', address: '浙江省杭州市滨江区西兴街道江陵路88号3幢' },
        { rowIndex: 8, parkName: '杭州湾信息港', enterpriseName: '杭州环特生物科技股份有限公司', creditCode: '9133010556612982F', registerDate: '2022-06-30至', address: '浙江省杭州市江干区江潮路88号杭州医药港9号楼' },
        { rowIndex: 9, parkName: '钱塘湾孵化器（一期）', enterpriseName: '杭州禾睿康宇医药科技科技有限公司', creditCode: '91330108MA2JG5GL9G', registerDate: '2022-06-30至', address: '浙江省杭州市滨江区西兴街道江陵路88号' },
        { rowIndex: 10, parkName: '钱塘湾孵化器（一期）', enterpriseName: '杭州腾品科技有限公司', creditCode: '9133010856673312C', registerDate: '2022-06-30至', address: '浙江省杭州市滨江区江南大道88号万轮科技园' },
        { rowIndex: 11, parkName: '菜鸟智谷产业园', enterpriseName: '杭州启明医疗器械股份有限公司', creditCode: '9133010856673312C', registerDate: '2022-06-30至', address: '浙江省杭州市滨江区江南大道699号' },
        { rowIndex: 12, parkName: '传化科创园', enterpriseName: '杭州艾名医学科技有限公司', creditCode: '91330108MA2JG5G55D', registerDate: '2022-06-30至', address: '浙江省杭州市滨江区西兴街道江陵路88号3幢' },
        { rowIndex: 13, parkName: '和达药谷园区', enterpriseName: '杭州环特生物科技股份有限公司', creditCode: '9133010556612982F', registerDate: '2022-06-30至', address: '浙江省杭州市江干区江潮路88号杭州医药港9号楼' },
        { rowIndex: 14, parkName: '颐高创业园', enterpriseName: '杭州禾睿康宇医药科技科技有限公司', creditCode: '91330108MA2JG5GL9G', registerDate: '2022-06-30至', address: '浙江省杭州市滨江区西兴街道江陵路88号' },
        { rowIndex: 15, parkName: '天和国际产业园', enterpriseName: '杭州腾品科技有限公司', creditCode: '9133010856673312C', registerDate: '2022-06-30至', address: '浙江省杭州市滨江区江南大道88号万轮科技园' }
      ],
      talentList: [
        { level: 'A类', name: '张明', company: '万轮科技', certDate: '2024-01-30', score: 5 },
        { level: 'B类', name: '李华', company: '传化科创', certDate: '2024-01-30', score: 2 },
        { level: 'B类', name: '王芳', company: '和达药谷', certDate: '2024-01-30', score: 2 }
      ],
      auditRecords: [
        { auditTime: '2024-01-15 10:30', auditor: '张三', auditLevel: '区县审核', result: '通过', opinion: '审核通过，符合要求' },
        { auditTime: '2024-01-20 14:20', auditor: '李四', auditLevel: '市级审核', result: '通过', opinion: '审核通过' }
      ]
    }
  },
  mounted() {
    const id = this.$route.params.id
    if (id) {
      this.fetchDetail(id)
    }
  },
  methods: {
    async fetchDetail(id) {
      try {
        const res = await getEvaluationDetail(id)
        this.evaluationInfo = res.data || {}
        this.auditForm.evaluationId = id
      } catch (e) {
        console.error('获取评价详情失败', e)
      }
    },

    switchTab(index) {
      const targetIndex = parseInt(index)
      const currentIndex = parseInt(this.activeIndex)
      
      if (targetIndex > currentIndex) {
        if (!this.stepCompleted[String(targetIndex)]) {
          this.$message.warning('请先进行上一步审核')
          return
        }
        this.activeIndex = String(index)
      } else {
        this.activeIndex = String(index)
      }
    },

    isStepCompleted(step) {
      if (this.stepCompleted[String(step)]) {
        return true
      }
      
      switch (step) {
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

    nextStep() {
      const current = parseInt(this.activeIndex)
      
      if (current < 9) {
        const nextIndex = current + 1
        
        if (!this.isStepCompleted(current)) {
          this.$message.warning('请先完成此内容审核')
          return
        }
        
        this.$confirm(`确定要进入第${nextIndex}步吗？`, '确认', {
          confirmButtonText: '确定',
          cancelButtonText: '取消',
          type: 'warning'
        }).then(() => {
          this.stepCompleted[String(nextIndex)] = true
          this.activeIndex = String(nextIndex)
        }).catch(() => {})
      }
    },

    prevStep() {
      const current = parseInt(this.activeIndex)
      if (current > 1) {
        this.activeIndex = String(current - 1)
      }
    },

    handleAudit(action) {
      this.auditDialogTitle = action === 1 ? '审核通过' : '审核驳回'
      this.auditForm.action = action
      this.auditDialogVisible = true
    },

    async confirmAudit() {
      if (!this.auditForm.finalOpinion) {
        this.$message.warning('请输入审核意见')
        return
      }
      this.submitting = true
      try {
        await submitAudit({
          evaluationId: this.auditForm.evaluationId,
          action: this.auditForm.action,
          opinion: this.auditForm.finalOpinion
        })
        this.$message.success(this.auditForm.action === 1 ? '审核通过成功' : '审核驳回成功')
        this.auditDialogVisible = false
        this.goBack()
      } catch (e) {
        console.error('审核失败', e)
      } finally {
        this.submitting = false
      }
    },

    confirmSubmit() {
      if (!this.auditForm.finalOpinion) {
        this.$message.warning('请输入审核意见')
        return
      }
      if (!this.auditForm.finalResult) {
        this.$message.warning('请选择审核结果')
        return
      }
      this.handleAudit(this.auditForm.finalResult === '1' ? 1 : 2)
    },

    saveDraft() {
      this.$message.success('保存成功')
    },

    showAuditRecord() {
      this.showRecordDialog = true
    },

    goBack() {
      this.$router.push('/district/audit')
    },

    getGradeTagType(grade) {
      const map = {
        'A': 'success',
        'B': '',
        'C': 'warning',
        'D': 'danger'
      }
      return map[grade] || 'info'
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
  align-items: center;
  padding: 16px 20px;
  background: #fff;
  border-bottom: 1px solid #eee;
}

.page-header .title {
  flex: 1;
  font-size: 18px;
  font-weight: 600;
  color: #303133;
  margin-left: 16px;
}

.header-actions {
  display: flex;
  gap: 10px;
}

.main-content {
  display: flex;
  height: calc(100vh - 140px);
}

.left-sidebar {
  width: 220px;
  background: #fff;
  border-right: 1px solid #eee;
  padding: 16px 0;
}

.audit-menu {
  border-right: none;
}

.audit-menu .el-menu-item {
  height: 44px;
  line-height: 44px;
  padding-left: 24px;
  font-size: 13px;
}

.audit-menu .el-menu-item.is-active {
  background: #e8f4fd;
  color: #1E40AF;
}

.right-content {
  flex: 1;
  padding: 20px;
  overflow-y: auto;
}

.tab-content {
  background: #fff;
  border-radius: 8px;
  padding: 20px;
}

.section-header {
  margin-bottom: 20px;
}

.section-title {
  font-size: 16px;
  font-weight: 600;
  color: #303133;
}

.info-card {
  margin-top: 0;
}

.form-item {
  margin-bottom: 24px;
}

.form-label {
  font-size: 14px;
  color: #606266;
  line-height: 1.8;
  display: block;
  margin-bottom: 12px;
}

.info-box {
  display: flex;
  align-items: flex-start;
  padding: 16px;
  background: #f0f9ff;
  border-radius: 4px;
  margin-bottom: 12px;
}

.info-box i {
  color: #409EFF;
  margin-right: 10px;
  font-size: 16px;
}

.info-box span {
  flex: 1;
  font-size: 14px;
  color: #606266;
  line-height: 1.6;
}

.checkbox-item {
  padding-top: 16px;
  border-top: 1px solid #eee;
}

.radio-group {
  display: flex;
  gap: 24px;
}

.file-list {
  margin-top: 12px;
}

.file-row {
  margin-top: 12px;
}

.file-item {
  display: flex;
  align-items: center;
  gap: 10px;
  padding: 10px 12px;
  background: #fafafa;
  border-radius: 4px;
  margin-bottom: 8px;
}

.file-item span {
  flex: 1;
  font-size: 13px;
  color: #606266;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}

.file-action {
  font-size: 13px;
  color: #409EFF;
}

.score-input {
  width: 180px;
}

.table-wrapper {
  margin-top: 12px;
}

.opinion-input {
  margin-top: 12px;
}

.grade-select {
  width: 150px;
}

.bottom-actions {
  display: flex;
  justify-content: center;
  gap: 20px;
  margin-top: 24px;
  padding-top: 20px;
  border-top: 1px solid #eee;
}

.bottom-actions .el-button {
  min-width: 100px;
}

@media (max-width: 1200px) {
  .radio-group {
    flex-wrap: wrap;
  }
}

@media (max-width: 768px) {
  .main-content {
    flex-direction: column;
  }
  .left-sidebar {
    width: 100%;
    border-right: none;
    border-bottom: 1px solid #eee;
  }
}
</style>