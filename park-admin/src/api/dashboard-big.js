import request from '@/utils/request'

// 使用阿里DataV地图数据中心的真实经纬度
const districtPositions = {
  '上城区': { lng: 120.171465, lat: 30.250236 },
  '拱墅区': { lng: 120.150053, lat: 30.314697 },
  '西湖区': { lng: 120.147376, lat: 30.272934 },
  '滨江区': { lng: 120.21062, lat: 30.206615 },
  '萧山区': { lng: 120.27069, lat: 30.162932 },
  '余杭区': { lng: 119.978959, lat: 30.27365 },
  '临平区': { lng: 120.299376, lat: 30.419025 },
  '钱塘区': { lng: 120.493972, lat: 30.322904 },
  '富阳区': { lng: 119.949869, lat: 30.049871 },
  '临安区': { lng: 119.715101, lat: 30.231153 },
  '桐庐县': { lng: 119.685045, lat: 29.797437 },
  '淳安县': { lng: 119.044276, lat: 29.604177 },
  '建德市': { lng: 119.279089, lat: 29.472284 }
}

const mockDistrictData = [
  {
    name: '上城区',
    parkCount: 68,
    manufacturingCount: 35,
    serviceCount: 33,
    buildArea: 8560.32,
    landArea: 215.48,
    enterpriseCount: 58,
    employeeCount: 1256,
    revenuePerMu: 2650,
    taxPerMu: 286,
    mainIndustry: '数字经济/金融服务/文创产业',
    nationalSpecializedCount: 12,
    provincialSpecializedCount: 38,
    innovativeSmeCount: 45,
    participateEnterpriseCount: 52
  },
  {
    name: '拱墅区',
    parkCount: 72,
    manufacturingCount: 38,
    serviceCount: 34,
    buildArea: 9230.56,
    landArea: 238.62,
    enterpriseCount: 65,
    employeeCount: 1380,
    revenuePerMu: 2480,
    taxPerMu: 268,
    mainIndustry: '智能制造/数字经济/文创产业',
    nationalSpecializedCount: 14,
    provincialSpecializedCount: 42,
    innovativeSmeCount: 52,
    participateEnterpriseCount: 58
  },
  {
    name: '西湖区',
    parkCount: 85,
    manufacturingCount: 42,
    serviceCount: 43,
    buildArea: 12580.24,
    landArea: 298.56,
    enterpriseCount: 82,
    employeeCount: 1860,
    revenuePerMu: 2890,
    taxPerMu: 315,
    mainIndustry: '数字经济/高新技术/文创产业',
    nationalSpecializedCount: 18,
    provincialSpecializedCount: 56,
    innovativeSmeCount: 68,
    participateEnterpriseCount: 72
  },
  {
    name: '滨江区',
    parkCount: 92,
    manufacturingCount: 48,
    serviceCount: 44,
    buildArea: 14250.88,
    landArea: 325.42,
    enterpriseCount: 95,
    employeeCount: 2150,
    revenuePerMu: 3120,
    taxPerMu: 348,
    mainIndustry: '数字经济/物联网/生物医药',
    nationalSpecializedCount: 22,
    provincialSpecializedCount: 68,
    innovativeSmeCount: 78,
    participateEnterpriseCount: 85
  },
  {
    name: '萧山区',
    parkCount: 105,
    manufacturingCount: 62,
    serviceCount: 43,
    buildArea: 16850.36,
    landArea: 385.28,
    enterpriseCount: 108,
    employeeCount: 2420,
    revenuePerMu: 2250,
    taxPerMu: 245,
    mainIndustry: '智能制造/高端装备/数字经济',
    nationalSpecializedCount: 16,
    provincialSpecializedCount: 52,
    innovativeSmeCount: 62,
    participateEnterpriseCount: 95
  },
  {
    name: '余杭区',
    parkCount: 88,
    manufacturingCount: 45,
    serviceCount: 43,
    buildArea: 13560.64,
    landArea: 318.45,
    enterpriseCount: 88,
    employeeCount: 1950,
    revenuePerMu: 2560,
    taxPerMu: 278,
    mainIndustry: '数字经济/智能制造/生物医药',
    nationalSpecializedCount: 15,
    provincialSpecializedCount: 48,
    innovativeSmeCount: 58,
    participateEnterpriseCount: 78
  },
  {
    name: '临平区',
    parkCount: 52,
    manufacturingCount: 32,
    serviceCount: 20,
    buildArea: 6850.48,
    landArea: 168.25,
    enterpriseCount: 48,
    employeeCount: 980,
    revenuePerMu: 2180,
    taxPerMu: 235,
    mainIndustry: '智能制造/装备制造',
    nationalSpecializedCount: 8,
    provincialSpecializedCount: 32,
    innovativeSmeCount: 38,
    participateEnterpriseCount: 42
  },
  {
    name: '钱塘区',
    parkCount: 62,
    manufacturingCount: 38,
    serviceCount: 24,
    buildArea: 8950.72,
    landArea: 215.68,
    enterpriseCount: 58,
    employeeCount: 1320,
    revenuePerMu: 2420,
    taxPerMu: 262,
    mainIndustry: '生物医药/智能制造/新材料',
    nationalSpecializedCount: 10,
    provincialSpecializedCount: 36,
    innovativeSmeCount: 42,
    participateEnterpriseCount: 52
  },
  {
    name: '富阳区',
    parkCount: 55,
    manufacturingCount: 35,
    serviceCount: 20,
    buildArea: 7250.36,
    landArea: 185.42,
    enterpriseCount: 52,
    employeeCount: 1080,
    revenuePerMu: 1980,
    taxPerMu: 215,
    mainIndustry: '智能制造/新材料/生物医药',
    nationalSpecializedCount: 6,
    provincialSpecializedCount: 28,
    innovativeSmeCount: 35,
    participateEnterpriseCount: 45
  },
  {
    name: '临安区',
    parkCount: 42,
    manufacturingCount: 28,
    serviceCount: 14,
    buildArea: 5680.24,
    landArea: 145.35,
    enterpriseCount: 42,
    employeeCount: 850,
    revenuePerMu: 1850,
    taxPerMu: 202,
    mainIndustry: '智能制造/装备制造/生物医药',
    nationalSpecializedCount: 4,
    provincialSpecializedCount: 22,
    innovativeSmeCount: 28,
    participateEnterpriseCount: 35
  },
  {
    name: '桐庐县',
    parkCount: 38,
    manufacturingCount: 25,
    serviceCount: 13,
    buildArea: 4850.56,
    landArea: 128.62,
    enterpriseCount: 38,
    employeeCount: 720,
    revenuePerMu: 1720,
    taxPerMu: 188,
    mainIndustry: '智能制造/生物医药/文创产业',
    nationalSpecializedCount: 3,
    provincialSpecializedCount: 18,
    innovativeSmeCount: 22,
    participateEnterpriseCount: 32
  },
  {
    name: '淳安县',
    parkCount: 28,
    manufacturingCount: 18,
    serviceCount: 10,
    buildArea: 3650.88,
    landArea: 95.36,
    enterpriseCount: 28,
    employeeCount: 520,
    revenuePerMu: 1580,
    taxPerMu: 168,
    mainIndustry: '生态经济/文旅产业/智能制造',
    nationalSpecializedCount: 2,
    provincialSpecializedCount: 12,
    innovativeSmeCount: 16,
    participateEnterpriseCount: 24
  },
  {
    name: '建德市',
    parkCount: 32,
    manufacturingCount: 22,
    serviceCount: 10,
    buildArea: 4250.44,
    landArea: 112.58,
    enterpriseCount: 32,
    employeeCount: 620,
    revenuePerMu: 1650,
    taxPerMu: 178,
    mainIndustry: '智能制造/生物医药/文旅产业',
    nationalSpecializedCount: 3,
    provincialSpecializedCount: 16,
    innovativeSmeCount: 20,
    participateEnterpriseCount: 28
  }
]

export function getBigScreenStats(year) {
  return new Promise(resolve => {
    setTimeout(() => {
      const stats = {
        parkTotal: 728,
        parkTotalChange: 7.34,
        employeeTotal: 13208,
        employeeTotalChange: 11.02,
        manufacturingCount: 415,
        manufacturingChange: -8.20,
        serviceCount: 313,
        serviceChange: 6.50,
        buildArea: 118956.32,
        buildAreaChange: 13.98,
        landArea: 2859.48,
        landAreaChange: 4.12,
        enterpriseTotal: 712,
        enterpriseTotalChange: 8.76,
        revenuePerMu: 1.56,
        revenuePerMuChange: 6.21,
        taxPerMu: 108.20,
        taxPerMuChange: -5.00,
        nationalSpecializedCount: 16,
        nationalSpecializedChange: 11.81,
        provincialSpecializedCount: 48,
        provincialSpecializedChange: 15.20,
        innovativeSmeCount: 86,
        innovativeSmeChange: 20.18,
        participateEnterpriseCount: 686,
        participateEnterpriseChange: 8.41
      }
      resolve({ code: 200, data: stats })
    }, 300)
  })
}

export function getDistrictData(year) {
  return new Promise(resolve => {
    setTimeout(() => {
      const data = mockDistrictData.map(item => ({
        ...item,
        position: [districtPositions[item.name].lng, districtPositions[item.name].lat]
      }))
      resolve({ code: 200, data })
    }, 300)
  })
}

export function getMuJunAnalysis(year) {
  return new Promise(resolve => {
    setTimeout(() => {
      const years = [2023, 2024, 2025, 2026]
      const data = years.map(y => ({
        year: y,
        // 亩均税收（万元/亩）
        manufacturing: 520 + (y - 2023) * 80 + Math.random() * 50,
        service: 720 + (y - 2023) * 60 + Math.random() * 40,
        average: 620 + (y - 2023) * 70 + Math.random() * 45,
        // 亩均产出（元/亩）
        manufacturingOut: 1850 + (y - 2023) * 200 + Math.random() * 100,
        serviceOut: 2580 + (y - 2023) * 180 + Math.random() * 120,
        averageOut: 2200 + (y - 2023) * 190 + Math.random() * 110
      }))
      resolve({ code: 200, data })
    }, 300)
  })
}

export function getPerformanceStats(year) {
  return new Promise(resolve => {
    setTimeout(() => {
      const districts = ['上城区', '拱墅区', '西湖区', '滨江区', '萧山区', '余杭区', '临平区', '钱塘区', '富阳区', '临安区', '桐庐县', '淳安县', '建德市']
      const data = districts.map(name => {
        const base = mockDistrictData.find(d => d.name === name) || {}
        return {
          name,
          service: Math.floor(base.parkCount * 0.45),
          manufacturing: Math.floor(base.parkCount * 0.55),
          gradeA: Math.floor(base.parkCount * 0.15),
          gradeB: Math.floor(base.parkCount * 0.35),
          gradeC: Math.floor(base.parkCount * 0.30),
          gradeD: Math.floor(base.parkCount * 0.20),
          taxPerMu: base.taxPerMu || 200,
          revenuePerMu: base.revenuePerMu || 2000
        }
      })
      resolve({ code: 200, data })
    }, 300)
  })
}

export function getStarRanking(year) {
  return new Promise(resolve => {
    setTimeout(() => {
      const data = [
        { name: '五星级园区', value: 100 },
        { name: '四星级园区', value: 200 },
        { name: '三星级园区', value: 300 },
        { name: '二星级园区', value: 128 }
      ]
      resolve({ code: 200, data })
    }, 300)
  })
}