package model.progress;

import os.OS;

public class Clock implements Runnable {

    private static final long TIMESLICE_LENGTH=6;//时间片长度

    public static final long TIMESLICE_UNIT=1000;//时间片单位(毫秒)

    private  long systemTime;//系统时钟

    private long restTime;//当前进程剩下的运行时间

    private CPU cpu;

    public Clock(){
        this.cpu= OS.cpu;
        init();
    }

    /**
     * 初始化时钟
     */
    public void init(){
        systemTime=0;
        restTime=TIMESLICE_LENGTH;
    }

    @Override
    public void run() {
        synchronized(this) {
            while (OS.launched) {
                //这边可能由有点小bug 看到时候最后的运行的调试。。
                    systemTime += TIMESLICE_UNIT / 1000;
                    restTime = (restTime + TIMESLICE_LENGTH - TIMESLICE_UNIT / 1000) % TIMESLICE_LENGTH;
                    //时间片到了
                    if (restTime == 0) {
                        cpu.lock.lock();
                        cpu.PSW=cpu.TIME_INTERMIT;
                        cpu.Ready();//运行转就绪
                        cpu.dispatch();//就绪转运行
                        cpu.lock.unlock();
                    }


            }
        }

    }

    /**
     * 获取系统时间
     * @return
     */

    public long getSystemTime() {

        return systemTime;
    }

    /**
     * 获取剩余时间
     * @return
     */
    public long getRestTime() {

        return restTime;
    }

}