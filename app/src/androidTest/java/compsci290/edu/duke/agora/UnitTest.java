package compsci290.edu.duke.agora;

import android.content.Context;
import android.widget.EditText;

import org.junit.Assert;
import org.junit.Test;

import static org.junit.Assert.*;
import compsci290.edu.duke.agora.MainActivity;
import compsci290.edu.duke.agora.MessagingActivity;
import compsci290.edu.duke.agora.QueueActivity;
import compsci290.edu.duke.agora.HomeActivity;
import compsci290.edu.duke.agora.SessionActivity;


/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
public class UnitTest {
    @Test
    public void addition_isCorrect() throws Exception {
        assertEquals(4, 2 + 2);
    }

    @Test
    public void mainActivityExist(){
        try {
            Class.forName("compsci290.edu.duke.agora.MainActivity");
        } catch (ClassNotFoundException e) {
            Assert.fail("should have a class called MainActivity");
        }
    }

    @Test
    public void homeActivityExists(){
        try {
            Class.forName("compsci290.edu.duke.agora.HomeActivity");
        } catch (ClassNotFoundException e) {
            Assert.fail("should have a class called HomeActivity");
        }
    }

    @Test
    public void sessionActivityExists(){
        try {
            Class.forName("compsci290.edu.duke.agora.SessionActivity");
        } catch (ClassNotFoundException e) {
            Assert.fail("should have a class called SessionActivity");
        }
    }

    @Test
    public void messagingActivityExists(){
        try {
            Class.forName("compsci290.edu.duke.agora.MessagingActivity");
        } catch (ClassNotFoundException e) {
            Assert.fail("should have a class called MessagingActivity");
        }
    }

    @Test
    public void queueActivityExists(){
        try {
            Class.forName("compsci290.edu.duke.agora.QueueActivity");
        } catch (ClassNotFoundException e) {
            Assert.fail("should have a class called QueueActivity");
        }
    }


}