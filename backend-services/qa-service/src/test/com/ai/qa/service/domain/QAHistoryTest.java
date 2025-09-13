package com.ai.qa.service.domain;


import com.ai.qa.service.domain.model.QAHistory;
import org.springframework.util.Assert;

public class QAHistoryTest {


    public void setup(){

    }

    @Test
    public void createNew(){

        QAHistory history =  QAHistory.createNew("","","");
        Assert.isNull(history.getUserId(),"");
    }

    @Test
    public void answerQuestion(){

        QAHistory history =  QAHistory.createNew("","","");
        String sss = history.getRAGAnswer();
        Assert.isTrue(sss.equals("adfa"),"");
    }


}
