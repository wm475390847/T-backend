package com.chaohu.qa.ttp.api.manager;

import com.chaohu.qa.ttp.api.dto.RequestDataDTO;
import com.chaohu.qa.ttp.api.dto.ThreadDataDTO;
import com.chaohu.qa.ttp.api.exception.ServiceException;
import lombok.extern.slf4j.Slf4j;
import org.apache.jmeter.config.Arguments;
import org.apache.jmeter.config.CSVDataSet;
import org.apache.jmeter.config.CSVDataSetBeanInfo;
import org.apache.jmeter.control.LoopController;
import org.apache.jmeter.control.gui.LoopControlPanel;
import org.apache.jmeter.control.gui.TestPlanGui;
import org.apache.jmeter.engine.StandardJMeterEngine;
import org.apache.jmeter.protocol.http.control.Header;
import org.apache.jmeter.protocol.http.control.HeaderManager;
import org.apache.jmeter.protocol.http.control.gui.HttpTestSampleGui;
import org.apache.jmeter.protocol.http.gui.HeaderPanel;
import org.apache.jmeter.protocol.http.sampler.HTTPSamplerProxy;
import org.apache.jmeter.report.config.ConfigurationException;
import org.apache.jmeter.report.dashboard.GenerationException;
import org.apache.jmeter.report.dashboard.ReportGenerator;
import org.apache.jmeter.reporters.ResultCollector;
import org.apache.jmeter.reporters.Summariser;
import org.apache.jmeter.samplers.SampleSaveConfiguration;
import org.apache.jmeter.save.SaveService;
import org.apache.jmeter.testbeans.gui.TestBeanGUI;
import org.apache.jmeter.testelement.TestElement;
import org.apache.jmeter.testelement.TestPlan;
import org.apache.jmeter.testelement.property.DoubleProperty;
import org.apache.jmeter.testelement.property.TestElementProperty;
import org.apache.jmeter.threads.ThreadGroup;
import org.apache.jmeter.threads.gui.ThreadGroupGui;
import org.apache.jmeter.timers.ConstantThroughputTimer;
import org.apache.jmeter.util.JMeterUtils;
import org.apache.jmeter.visualizers.ViewResultsFullVisualizer;
import org.apache.jorphan.collections.HashTree;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Component;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Map;
import java.util.Optional;

import static org.apache.jmeter.JMeter.JMETER_REPORT_OUTPUT_DIR_PROPERTY;

/**
 * @author wangmin
 * @date 2023/3/31 09:44
 */
@Slf4j
@Component
public class JmeterManager {

    @Value("${jmeter.encoding}")
    private String jmeterEncoding;
    @Value("${jmeter.home}")
    private String jmeterHome;
    private final String jmeterFileFolder = System.getProperty("user.dir") + "/file/";

    /**
     * 获取jmeter引擎
     *
     * @param jmxFile 测试文件
     * @return jmeter引擎
     */
    public StandardJMeterEngine getJmeterEngine(File jmxFile) {
        initJmeterHome();
        StandardJMeterEngine engine = new StandardJMeterEngine();
        try {
            // 将file文件转化为hashTree
            HashTree hashTree = SaveService.loadTree(jmxFile);
            engine.configure(hashTree);
            Thread.sleep(500);
        } catch (Exception e) {
            log.error("执行错误: {}", e.getMessage());
            e.printStackTrace();
        }
        return engine;
    }

    /**
     * 创建测试计划
     *
     * @param requestDataDTO http请求数据
     * @param threadDataDTO  线程组数据
     * @return 测试计划
     */
    public HashTree createTestPlanTree(RequestDataDTO requestDataDTO, ThreadDataDTO threadDataDTO) {
        initJmeterHome();
        // 获取TestPlan
        TestPlan testPlan = getTestPlan();
        // 获取设置循环控制器
        LoopController loopController = getLoopController(threadDataDTO);
        // 获取线程组
        ThreadGroup threadGroup = getThreadGroup(loopController, threadDataDTO);
        // 获取请求头信息
        HeaderManager headerManager = getHeaderManager(requestDataDTO.getHeaders());
        // 获取Http请求信息
        HTTPSamplerProxy httpSamplerProxy = getHttpSamplerProxy(requestDataDTO);
        // 获取结果：如汇总报告、察看结果树
        ResultCollector resultCollector = getResultCollector();

        // 设置测试计划数，先添加测试计划、线程组
        HashTree testPlanTree = new HashTree();
        testPlanTree.add(testPlan);
        HashTree threadGroupHashTree = testPlanTree.add(testPlan, threadGroup);

        // 获取设置吞吐量
        if (threadDataDTO.getThroughputTimer() != null) {
            ConstantThroughputTimer constantThroughputTimer = getConstantThroughputTimer(threadDataDTO.getThroughputTimer());
            // 如果有吞吐量就添加到线程组树中
            threadGroupHashTree.add(constantThroughputTimer);
        }

        // 现场组树添加请求头、http请求
        threadGroupHashTree.add(headerManager);
        threadGroupHashTree.add(httpSamplerProxy);
        testPlanTree.add(testPlanTree.getArray()[0], resultCollector);
        return testPlanTree;
    }

    /**
     * 生成报告
     *
     * @param fileFolder 报告输出文件夹
     */
    public void generatorReport(String fileFolder) {
        try {
            String reportFolder = jmeterFileFolder + fileFolder + "/";
            log.info("报告生成地址: {}", reportFolder);
            ReportGenerator reportGenerator = new ReportGenerator(jmeterFileFolder + "result.jtl", null);
            JMeterUtils.setProperty(JMETER_REPORT_OUTPUT_DIR_PROPERTY, reportFolder);
            reportGenerator.generate();
        } catch (ConfigurationException | GenerationException e) {
            log.info("报告生成错误: {}", e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * 监听结果
     *
     * @return 结果数组
     */
    private ResultCollector getResultCollector() {
        Summariser summariser = new Summariser("结果");
        ResultCollector resultCollector = new ResultCollector(summariser);
        resultCollector.setName("查看结果树");
        resultCollector.setErrorLogging(false);
        resultCollector.setEnabled(true);
        resultCollector.setFilename(jmeterFileFolder + "result.jtl");
        resultCollector.setSaveConfig(getSampleSaveConfig());
        resultCollector.setProperty(TestElement.GUI_CLASS, ViewResultsFullVisualizer.class.getName());
        return resultCollector;
    }

    /**
     * 结果文件包含的内容
     *
     * @return 结果配置
     */
    private SampleSaveConfiguration getSampleSaveConfig() {
        SampleSaveConfiguration sampleSaveConfiguration = new SampleSaveConfiguration();
        sampleSaveConfiguration.setTime(true);
        sampleSaveConfiguration.setLatency(true);
        sampleSaveConfiguration.setTimestamp(true);
        sampleSaveConfiguration.setSuccess(true);
        sampleSaveConfiguration.setLabel(true);
        sampleSaveConfiguration.setCode(true);
        sampleSaveConfiguration.setMessage(true);
        sampleSaveConfiguration.setThreadName(true);
        sampleSaveConfiguration.setDataType(true);
        sampleSaveConfiguration.setAssertions(true);
        sampleSaveConfiguration.setSubresults(true);
        sampleSaveConfiguration.setResponseData(true);
        sampleSaveConfiguration.setFieldNames(true);
        sampleSaveConfiguration.setRequestHeaders(true);
        sampleSaveConfiguration.setAssertionResultsFailureMessage(true);
        sampleSaveConfiguration.setRequestHeaders(true);
        sampleSaveConfiguration.setAssertionResultsFailureMessage(true);
        sampleSaveConfiguration.setEncoding(false);
        sampleSaveConfiguration.setSamplerData(false);
        sampleSaveConfiguration.setAsXml(false);
        sampleSaveConfiguration.setResponseHeaders(false);
        return sampleSaveConfiguration;
    }

    /**
     * 创建http请求信息
     *
     * @param requestDataDTO http请求
     * @return HTTPSamplerProxy
     */
    private HTTPSamplerProxy getHttpSamplerProxy(RequestDataDTO requestDataDTO) {
        Optional.ofNullable(requestDataDTO.getUrl()).orElseThrow(() -> new ServiceException("URL不能为空"));
        HTTPSamplerProxy httpSamplerProxy = new HTTPSamplerProxy();
        httpSamplerProxy.setName("HTTP请求");
        httpSamplerProxy.setMethod(requestDataDTO.getMethod().name());
        try {
            URL url = new URL(requestDataDTO.getUrl());
            if (requestDataDTO.getMethod().equals(HttpMethod.POST)) {
                httpSamplerProxy.addNonEncodedArgument("", requestDataDTO.getRequestBody(), "");
                httpSamplerProxy.setPostBodyRaw(true);
            }
            int port = (url.getPort() == -1 || url.getPort() == 80) ? 80 : 443;
            httpSamplerProxy.setProtocol(url.getProtocol());
            httpSamplerProxy.setPath(url.getPath());
            httpSamplerProxy.setDomain(url.getHost());
            httpSamplerProxy.setPort(port);
        } catch (MalformedURLException e) {
            log.error("创建HTTP请求失败: {}", e.getMessage());
            e.printStackTrace();
        }

        httpSamplerProxy.setContentEncoding(jmeterEncoding);
        httpSamplerProxy.setFollowRedirects(true);
        httpSamplerProxy.setUseKeepAlive(true);
        httpSamplerProxy.setEnabled(true);
        httpSamplerProxy.setAutoRedirects(false);
        httpSamplerProxy.setProperty(TestElement.GUI_CLASS, HttpTestSampleGui.class.getName());
        httpSamplerProxy.setProperty(TestElement.TEST_CLASS, HTTPSamplerProxy.class.getName());
        httpSamplerProxy.setEmbeddedUrlRE("");
        httpSamplerProxy.setConnectTimeout("");
        httpSamplerProxy.setResponseTimeout("");
        return httpSamplerProxy;
    }

    /**
     * 获取线程组
     *
     * @param loopController 循环控制器
     * @param threadDataDTO  线程组的设置
     * @return 线程组
     */
    private ThreadGroup getThreadGroup(LoopController loopController, ThreadDataDTO threadDataDTO) {
        ThreadGroup threadGroup = new ThreadGroup();
        threadGroup.setName("线程组");
        threadGroup.setNumThreads(threadDataDTO.getNumThreads() == null ? 1 : threadDataDTO.getNumThreads());
        threadGroup.setRampUp(threadDataDTO.getRampUp() == null ? 1 : threadDataDTO.getRampUp());
        threadGroup.setDuration(threadDataDTO.getDuration() == null ? 0 : threadDataDTO.getDuration());
        threadGroup.setDelay(threadDataDTO.getDelay() == null ? 0 : threadDataDTO.getDelay());
        threadGroup.setScheduler(false);
        threadGroup.setEnabled(true);
        threadGroup.setSamplerController(loopController);
        threadGroup.setProperty(TestElement.TEST_CLASS, ThreadGroup.class.getName());
        threadGroup.setProperty(TestElement.GUI_CLASS, ThreadGroupGui.class.getName());
        threadGroup.setProperty(ThreadGroup.ON_SAMPLE_ERROR, "continue");
        threadGroup.setProperty(new TestElementProperty(ThreadGroup.MAIN_CONTROLLER, loopController));
        return threadGroup;
    }

    /**
     * 获取循环控制器
     *
     * @param threadDataDTO 线程组数据
     * @return 循环控制器
     */
    private LoopController getLoopController(ThreadDataDTO threadDataDTO) {
        LoopController loopController = new LoopController();
        loopController.setName("循环控制器");
        loopController.setLoops(threadDataDTO.getLoops() == null ? 1 : threadDataDTO.getLoops());
        loopController.setEnabled(true);
        loopController.setContinueForever(false);
        loopController.setProperty(TestElement.GUI_CLASS, LoopControlPanel.class.getName());
        loopController.setProperty(TestElement.TEST_CLASS, LoopController.class.getName());
        return loopController;
    }

    /**
     * 获取测试计划
     *
     * @return 测试计划
     */
    private TestPlan getTestPlan() {
        TestPlan testPlan = new TestPlan("测试计划");
        testPlan.setFunctionalMode(false);
        testPlan.setSerialized(false);
        testPlan.setTearDownOnShutdown(true);
        testPlan.setEnabled(true);
        testPlan.setComment("");
        testPlan.setTestPlanClasspath("");
        testPlan.setProperty(TestElement.TEST_CLASS, TestPlan.class.getName());
        testPlan.setProperty(TestElement.GUI_CLASS, TestPlanGui.class.getName());
        Arguments arguments = new Arguments();
        testPlan.setUserDefinedVariables(arguments);
        return testPlan;
    }

    /**
     * 获取HTTP请求头管理器
     *
     * @param headers 请求头
     * @return HTTP信息头管理器
     */
    private HeaderManager getHeaderManager(Map<String, String> headers) {
        HeaderManager headerManager = new HeaderManager();
        headerManager.setName("HTTP信息头管理器");
        headers.forEach((k, v) -> headerManager.add(new Header(k, v)));
        headerManager.setEnabled(true);
        headerManager.setProperty(TestElement.TEST_CLASS, HeaderManager.class.getName());
        headerManager.setProperty(TestElement.GUI_CLASS, HeaderPanel.class.getName());
        return headerManager;
    }

    /**
     * 获取scv数据集合
     *
     * @param bodyPath 请求体路径
     * @return csv数据集合
     */
    private CSVDataSet getCsvDataSet(String bodyPath) {
        CSVDataSet csvDataSet = new CSVDataSet();
        csvDataSet.setName("body请求体");
        csvDataSet.setEnabled(true);
        csvDataSet.setFilename(bodyPath);
        csvDataSet.setFileEncoding(jmeterEncoding);
        csvDataSet.setIgnoreFirstLine(false);
        csvDataSet.setQuotedData(true);
        csvDataSet.setRecycle(true);
        csvDataSet.setStopThread(false);
        csvDataSet.setVariableNames("");
        csvDataSet.setDelimiter(",");
        csvDataSet.setShareMode(CSVDataSetBeanInfo.getShareTags()[0]);
        csvDataSet.setProperty(TestElement.TEST_CLASS, CSVDataSet.class.getName());
        csvDataSet.setProperty(TestElement.GUI_CLASS, TestBeanGUI.class.getName());
        return csvDataSet;
    }

    /**
     * 限制QPS设置
     *
     * @param throughputTimer 吞吐量
     * @return 常数吞吐量定时器
     */
    private ConstantThroughputTimer getConstantThroughputTimer(int throughputTimer) {
        ConstantThroughputTimer constantThroughputTimer = new ConstantThroughputTimer();
        constantThroughputTimer.setName("常数吞吐量定时器");
        constantThroughputTimer.setEnabled(true);
        constantThroughputTimer.setCalcMode(ConstantThroughputTimer.Mode.AllActiveThreads.ordinal());
        constantThroughputTimer.setProperty(TestElement.TEST_CLASS, ConstantThroughputTimer.class.getName());
        constantThroughputTimer.setProperty(TestElement.GUI_CLASS, TestBeanGUI.class.getName());
        constantThroughputTimer.setCalcMode(ConstantThroughputTimer.Mode.AllActiveThreads.ordinal());
        DoubleProperty doubleProperty = new DoubleProperty();
        doubleProperty.setName("吞吐量");
        doubleProperty.setValue(throughputTimer * 60f);
        constantThroughputTimer.setProperty(doubleProperty);
        return constantThroughputTimer;
    }

    private void initJmeterHome() {
        JMeterUtils.setJMeterHome(jmeterHome);
        JMeterUtils.loadJMeterProperties(JMeterUtils.getJMeterBinDir() + "/jmeter.properties");
        JMeterUtils.initLocale();
    }
}
