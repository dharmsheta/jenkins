package hudson.model.queue;

import hudson.model.FreeStyleProject;
import hudson.model.Queue.Item;

import org.jvnet.hudson.test.HudsonTestCase;

public class QueueTaskDispatcherTest extends HudsonTestCase {

    @SuppressWarnings("deprecation")
    public void testCanRunBlockageIsDisplayed() throws Exception {
        QueueTaskDispatcher.all().add(new QueueTaskDispatcher() {
            @Override
            public CauseOfBlockage canRun(Item item) {
                return new CauseOfBlockage() {

                    @Override
                    public String getShortDescription() {
                        return "blocked by canRun";
                    }
                };
            }
        });
        FreeStyleProject project = createFreeStyleProject();
        jenkins.getQueue().schedule(project);

        Item item = jenkins.getQueue().getItem(project);
        for (int i = 0; i < 4 * 60 && !item.isBlocked(); i++) {
            Thread.sleep(250);
            item = jenkins.getQueue().getItem(project);
        }
        assertTrue("Not blocked after 60 seconds", item.isBlocked());
        assertEquals("Expected CauseOfBlockage to be returned", "blocked by canRun", item.getWhy());
    }
}
