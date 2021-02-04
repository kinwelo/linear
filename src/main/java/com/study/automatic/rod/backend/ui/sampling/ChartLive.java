package com.study.automatic.rod.backend.ui.sampling;

import com.github.appreciated.apexcharts.ApexCharts;
import com.github.appreciated.apexcharts.ApexChartsBuilder;
import com.github.appreciated.apexcharts.config.Tooltip;
import com.github.appreciated.apexcharts.config.builder.*;
import com.github.appreciated.apexcharts.config.chart.Type;
import com.github.appreciated.apexcharts.config.chart.animations.Easing;
import com.github.appreciated.apexcharts.config.chart.animations.builder.DynamicAnimationBuilder;
import com.github.appreciated.apexcharts.config.chart.builder.AnimationsBuilder;
import com.github.appreciated.apexcharts.config.chart.builder.ToolbarBuilder;
import com.github.appreciated.apexcharts.config.chart.builder.ZoomBuilder;
import com.github.appreciated.apexcharts.config.subtitle.Align;
import com.github.appreciated.apexcharts.config.tooltip.builder.XBuilder;
import com.github.appreciated.apexcharts.config.tooltip.builder.YBuilder;
import com.github.appreciated.apexcharts.config.tooltip.y.Title;
import com.github.appreciated.apexcharts.config.xaxis.builder.TitleBuilder;
import com.github.appreciated.apexcharts.config.yaxis.builder.LabelsBuilder;
import com.github.appreciated.apexcharts.helper.Series;
import com.vaadin.flow.component.AttachEvent;
import com.vaadin.flow.component.DetachEvent;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;

import java.time.format.DateTimeFormatterBuilder;
import java.util.ArrayList;
import java.util.logging.Formatter;

//
//@Route("stream")
//@Push
public class ChartLive extends VerticalLayout {   // extends ExampleHolderView
    boolean clearChartData = true;
    static private boolean isThreadRunning = false;
    private double nextValueDouble = -200;
//
    /*private double oYmin = 0;
    private double oYmax = 100;
    private double oYtick = 10;*/

    private final ApexCharts chart;
    private Thread thread;

    public ChartLive(String title, String seriesTitle) {
        setSizeFull();

        chart = ApexChartsBuilder.get().withChart(ChartBuilder.get()
                .withType(Type.area)
                .withAnimations(AnimationsBuilder.get()
                        .withEnabled(true)
                        .withEasing(Easing.linear)
                        .withDynamicAnimation(DynamicAnimationBuilder.get()
                                .withSpeed(1000)
                                .build())
                        .build())
                .withToolbar(ToolbarBuilder.get().withShow(false).build())
                .withZoom(ZoomBuilder.get().withEnabled(false).build())
                .build())
                .withTooltip(TooltipBuilder.get()
                        .withX(XBuilder.get()
                                .withShow(false)
                                .build())
                        .withY(YBuilder.get()
                                .withFormatter("(val) => val.toLocaleString('fr',{ minimumFractionDigits: 2, maximumFractionDigits: 4})")
                                .build())
                        .build())
                .withDataLabels(DataLabelsBuilder.get()
                        .withEnabled(false)
                        .build())
                .withXaxis(XAxisBuilder.get()
                        .withTitle(TitleBuilder.get()
                                .withText("Sekunda pomiaru")
                                .build())
                        .withRange(10.0).build())
                /*.withYaxis(YAxisBuilder.get()
                        .withMin(oYmin)
                        .withMax(oYmax)
                        .withTickAmount(oYtick)
                        .build())*/
                .withYaxis(YAxisBuilder.get()
                        .withLabels(LabelsBuilder.get()
                                .withFormatter("(val) => val.toLocaleString('fr',{ minimumFractionDigits: 2, maximumFractionDigits: 2})")
                                .build())
                        .withTitle(com.github.appreciated.apexcharts.config.yaxis.builder.TitleBuilder.get()
                                .withText("Długość [mm]")
                                .build())
                        .build())
                .withSeries(new Series<>(seriesTitle,0), new Series<>("T", 0))
                .withTitle(TitleSubtitleBuilder.get()
                     .withText(title)
                     .withAlign(Align.center)
                    .build())
                .build();

        chart.setDebug(true);
        add(chart);
    }

    @Override
    protected void onAttach(AttachEvent attachEvent) {
        super.onAttach(attachEvent);
        thread = new Thread(() -> {
            ArrayList<Double> arrayList = new ArrayList<>();
            arrayList.add(0.0);
            while (true) {
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                if(isThreadRunning){
                    //clear chart before next run
                    if(clearChartData){
                        arrayList.clear();
                        clearChartData = false;
                    }
                    arrayList.add(nextValueDouble);
                    getUI().ifPresent(ui -> ui.access(() -> chart.updateSeries(new Series<>(arrayList.toArray(new Double[]{})))));
                } else {
                    //po zakończeniu symulacji ustaw flagę czyszczenia wykresu
                    clearChartData = true;
                }
                //save actual status to previous
            }
        });
        thread.start();
    }

    @Override
    protected void onDetach(DetachEvent detachEvent) {
        super.onDetach(detachEvent);
        thread.interrupt();
        isThreadRunning = false;
    }

    public double getNextValueDouble() {
        return nextValueDouble;
    }

    public void setNextValueDouble(double nextValueDouble) {
        this.nextValueDouble = nextValueDouble;
    }

    public boolean isRunThread() {
        return isThreadRunning;
    }

    public void setRunThread(boolean runThread) {
        isThreadRunning = runThread;
    }

    public void clearChartData(){this.clearChartData = true;}
}