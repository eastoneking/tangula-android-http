package com.tangula.android.http

import com.tangula.android.test.http.HttpBaseTest
import org.junit.Assert.assertEquals
import org.junit.Test
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse


class HttpUtilsTest : HttpBaseTest() {

    @Test
    fun testPostBizSucessA() {
        val res = "post"
        assertEquals(testPostInServer<String>(18080, "/*",
                /*
                 *服务端模拟处理
                 */
                { _: HttpServletRequest, resp: HttpServletResponse ->
                        val out = resp.outputStream
                        out.println("{'status':0, body:{'a':'$res'}}")
                        out.flush()
                })
        /*
         * 访问服务端，并处理应答
         */
        { fp ->
            postBizSucessA("http://localhost:18080", object {},
                    Map::class.java as Class<Map<String, Object>>) { map: Map<String, Object>? ->
                fp.succeeded(map?.get("a") as String) //通知测试程序继续执行
            }
        }, res)
    }


    @Test
    fun testPutBizSucessA() {
        val res = "put"
        assertEquals(testPutInServer<String>(18080, "/*", { _: HttpServletRequest, resp: HttpServletResponse ->
                val out = resp.outputStream
                out.println("{'status':0, body:{'a':'$res'}}")
                out.flush()
        }) { fp ->
            putBizSucessA("http://localhost:18080", object {},
                    Map::class.java as Class<Map<String, Object>>) { map: Map<String, Object>? ->
                fp.succeeded(map?.get("a") as String)
            }
        }, res)
    }


    @Test
    fun testDeleteBizSucessA() {
        val res = "delete"
        assertEquals(testDeleteInServer<String>(18080, "/*", { _: HttpServletRequest, resp: HttpServletResponse ->
                val out = resp.outputStream
                out.println("{'status':0, body:{'a':'$res'}}")
                out.flush()
        }) { fp ->
            deleteBizSucessA("http://localhost:18080", object {},
                    Map::class.java as Class<Map<String, Object>>) { map: Map<String, Object>? ->
                fp.succeeded(map?.get("a") as String)
            }
        }, res)
    }


    @Test
    fun testGetBizSucessA() {
        val res = "get"
        assertEquals(testGetInServer<String>(18080, "/*", { _: HttpServletRequest, resp: HttpServletResponse ->
                val out = resp.outputStream
                out.println("{'status':0, body:{'a':'$res'}}")
                out.flush()

        }) { fp ->
            getBizSucessA("http://localhost:18080", object {},
                    Map::class.java as Class<Map<String, Object>>) { map: Map<String, Object>? ->
                fp.succeeded(map?.get("a") as String)
            }
        }, res)
    }


}