package cz.hartrik.pia.controller

import com.fasterxml.jackson.databind.ObjectMapper
import org.junit.Ignore
import org.springframework.http.MediaType
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.ResultActions
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders

/**
 *
 * @version 2017-05-03
 * @author Patrik Harag
 */
@Ignore("Helper class")
class Helper {

    private static JSON_UTF8 = MediaType.parseMediaType("application/json;charset=UTF-8")

    private static toJsonBytes(object) {
        ObjectMapper mapper = new ObjectMapper()
        return mapper.writeValueAsBytes(object)
    }

    static ResultActions post(MockMvc mock, url, obj) {
        mock.perform(MockMvcRequestBuilders
                .post(url)
                .contentType(JSON_UTF8)
                .content(toJsonBytes(obj)))
    }

    static ResultActions get(MockMvc mock, url) {
        mock.perform(MockMvcRequestBuilders
                .get(url)
                .accept(JSON_UTF8))
    }

}
