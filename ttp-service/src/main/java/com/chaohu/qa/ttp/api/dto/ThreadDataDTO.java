package com.chaohu.qa.ttp.api.dto;

import lombok.Data;
import lombok.experimental.Accessors;

/**
 * 线程组的相关设置
 *
 * @author wangmin
 * @date 2023/3/30 17:12
 */
@Data
@Accessors(chain = true)
public class ThreadDataDTO {

    /**
     * 线程组数量
     */
    private Integer numThreads;

    /**
     * 循环次数
     */
    private Integer loops;

    /**
     * Ramp-Up表示多少时间内启动线程，比如线程数100，Ramp-Up设置为10，表示10秒内启动100线程，不一定是每秒启动10个线程；
     * 单位s
     */
    private Integer rampUp;

    /**
     * 持续时间，限制测试的时间
     * 单位s
     */
    private Integer duration;

    /**
     * 执行间隔，当有多个jmx文件需要执行时可以控制当前jmx延迟执行
     * 单位s
     */
    private Integer delay;

    /**
     * Throughput Shaping Timer 是用来控制吞吐量的定时器，通过延缓线程运行来整体控制取样器产生的RPS。
     */
    private Integer throughputTimer;
}
