package admin;

import com.nageoffer.project.dto.req.ShortLinkCreateReqDTO;
import org.junit.Test;

import javax.annotation.Resource;


public class RestoreTest {

    @Resource
    private ShortLinkCreateReqDTO  shortLinkCreateReqDTO;

    @Test
    public void createTest(){
        shortLinkCreateReqDTO.setOriginUrl("\"http://github.com\"");
        shortLinkCreateReqDTO.setDomain("http://nurl.ink");
        shortLinkCreateReqDTO.setGid("\"7wsMcI\"");

        System.out.println(shortLinkCreateReqDTO);
    }
}
