package com.chaohu.qa.ttp.web;

import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class TtpBackedApplicationTests {

//    @Resource
//    private PerformanceController performanceController;
//    @Resource
//    private IPerformanceService performanceService;
//    /**
//     * 创建一个压测任务
//     */
//    @Test
//    public void test1() {
//        ThreadDataDTO threadDataDTO = new ThreadDataDTO()
//                .setNumThreads(1)
//                .setLoops(1);
//        RequestDataDTO requestDataDTO = new RequestDataDTO();
//        requestDataDTO.setUrl("http://test-yuanmao.shuwen.com/business/api/meta/v1/qq/coupons");
//        requestDataDTO.setMethod(HttpMethod.POST);
//        JSONObject requestBody = new JSONObject();
//        requestBody.put("code", "");
//        requestBody.put("openid", "");
//        requestBody.put("attach", "");
//        requestBody.put("card_id", "");
//        requestDataDTO.setRequestBody(JSONObject.toJSONString(requestBody));
//        HashMap<String, String> hashMap = new HashMap<>();
//        hashMap.put("voucher", "eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzI1NiJ9.eyJ1aWQiOiIyOTY0IiwiZXhwIjoxNjgyNDE0Nzk4OTk5LCJpYXQiOjE2NzcyMzA3OTg5OTksInYiOiJob25ncWkifQ.kWZc0GH_Zh_B18O7OxZl7NnDSS3Zufszpi56t_-Esp4");
//        hashMap.put("Content-type", "application/json");
//        requestDataDTO.setHeaders(hashMap);
//        requestDataDTO.setPort(80);
//
//        PerformanceCreateReq performanceCreateReq = new PerformanceCreateReq();
//        performanceCreateReq.setPerformanceName("红旗红包接口压测");
//        performanceCreateReq.setRequestData(requestDataDTO);
//        performanceCreateReq.setThreadGroupData(threadDataDTO);
//        performanceService.create(performanceCreateReq);
//    }
//
//    /**
//     * 修改压测任务
//     */
//    @Test
//    public void test2() {
//        PerformanceInfo performanceInfo = performanceInfoMapper.selectByPrimaryKey(1);
//        RequestDataDTO requestDataDTO = JSONObject.toJavaObject(performanceInfo.getRequestData(), RequestDataDTO.class);
//        ThreadDataDTO threadDataDTO = JSONObject.toJavaObject(performanceInfo.getThreadData(), ThreadDataDTO.class);
//        threadDataDTO.setNumThreads(2).setLoops(2);
//        PerformanceUpdateReq performanceUpdateReq = new PerformanceUpdateReq();
//        performanceUpdateReq.setId(performanceInfo.getId());
//        performanceUpdateReq.setRequestData(requestDataDTO);
//        performanceUpdateReq.setThreadData(threadDataDTO);
//        performanceUpdateReq.setPerformanceName(performanceInfo.getPerformanceName());
//        performanceService.update(performanceUpdateReq);
//    }
//
//    /**
//     * 执行压测任务
//     */
//    @Test
//    public void test3() {
//        performanceService.start(1);
//    }
//
//    @Test
//    public void test4() {
//        QueryWrapper<PerformanceInfo> wrapper = new QueryWrapper<>();
//        List<PerformanceInfo> performanceInfos = performanceInfoMapper.selectList(wrapper);
//        performanceInfos.forEach(e -> System.err.println(e));
//
//        PerformanceInfo performanceInfo = performanceInfoMapper.selectByPrimaryKey(1);
//        System.err.println(performanceInfo);
//    }
//
//    @Test
//    public void test5() {
//        Result<Object> result = performanceController.list(1, 1, "红包");
//        System.err.println(result);
//    }
}
