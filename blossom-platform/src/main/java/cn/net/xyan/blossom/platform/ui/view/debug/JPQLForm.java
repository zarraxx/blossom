package cn.net.xyan.blossom.platform.ui.view.debug;

import cn.net.xyan.blossom.core.i18n.TR;
import cn.net.xyan.blossom.core.utils.ExceptionUtils;
import com.vaadin.data.Container;

import com.vaadin.ui.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.vaadin.aceeditor.AceEditor;
import org.vaadin.aceeditor.AceMode;

import java.util.LinkedList;
import java.util.List;

/**
 * Created by zarra on 16/6/8.
 */
public class JPQLForm extends CustomComponent {

    AceEditor textArea;

    Table table;

    Button bRun;

    VerticalSplitPanel mainPanel ;

    HorizontalLayout buttonLayout;

    Label lInfo ;

    List<RunSQLListener> listeners = new LinkedList<>();

    Logger logger = LoggerFactory.getLogger(JPQLForm.class);

    public class RunSQLEvent{
        public RunSQLEvent(JPQLForm form, String sql) {
            this.form = form;
            this.sql = sql;
        }

        String sql;
        JPQLForm form;
    }

    interface RunSQLListener{
        void run(RunSQLEvent event);
    }

    public JPQLForm() {
        super();


        mainPanel = new VerticalSplitPanel();

        VerticalLayout editArea = new VerticalLayout();

        textArea = new AceEditor();

        textArea.setWidth("100%");





        buttonLayout = new HorizontalLayout();

        bRun = new Button(TR.m(TR.Run, "Run"));

        lInfo = new Label("");

        buttonLayout.addComponent(bRun);
        buttonLayout.addComponent(lInfo);
        buttonLayout.setComponentAlignment(lInfo,Alignment.BOTTOM_CENTER);
        buttonLayout.setSpacing(true);

        editArea.addComponent(buttonLayout);
        editArea.addComponent(textArea);

        editArea.setExpandRatio(textArea,1);

        textArea.setHeight("800px");

        editArea.setSpacing(true);

        mainPanel.setFirstComponent(editArea);

        table = new Table("Result:");

        table.setSizeFull();

        mainPanel.setSecondComponent(table);

        bRun.addClickListener(new Button.ClickListener() {
            @Override
            public void buttonClick(Button.ClickEvent event) {
                String sql = textArea.getValue();

                sql = parseSQL(sql);

                bRun.setComponentError(null);

                RunSQLEvent e = new RunSQLEvent(JPQLForm.this,sql);
                for(RunSQLListener listener:listeners){
                    try {
                        listener.run(e);
                    }catch (Throwable throwable){
                        ExceptionUtils.traceError(throwable,logger);
                    }
                }

            }
        });

        setupAceEdit();

        mainPanel.setSplitPosition(20f);
        setCompositionRoot(mainPanel);
    }

    public void setupAceEdit(){
        textArea.setThemePath("/static/ace");
        textArea.setModePath("/static/ace");
        textArea.setWorkerPath("/static/ace");

        textArea.setMode(AceMode.sql);
    }

    public void addRunListener(RunSQLListener runSQLListener){
        listeners.add(runSQLListener);
    }

    public void removeRunListener(RunSQLListener runSQLListener){
        listeners.remove(runSQLListener);
    }

    public boolean existRunListener(RunSQLListener runSQLListener){
        return listeners.contains(runSQLListener);
    }

    public String parseSQL(String jpql){
        String[] lines = jpql.split("\n");
        StringBuffer stringBuffer = new StringBuffer();
        for (String line:lines){
            line = line.trim();
            if (line.startsWith("--") || line.startsWith("#") || line.startsWith("//")){
                continue;
            }
            stringBuffer.append(line);
            stringBuffer.append(" ");
        }

        return stringBuffer.toString();
    }

    public void setError(String msg){

    }

    public void setResult(Container container,long cost){
        int size = container.size();

        String msg = String.format("Get %d row cost %d ms",size,cost);
        lInfo.setValue(msg);
        table.setContainerDataSource(container);
        table.refreshRowCache();
    }
}
