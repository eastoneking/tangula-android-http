package com.tangula.android.test.http

import com.tangula.android.http.HttpBaseKotlin
import org.eclipse.jetty.server.Server
import org.eclipse.jetty.servlet.ServletHandler
import org.eclipse.jetty.servlet.ServletHolder
import org.eclipse.jetty.util.FuturePromise
import javax.servlet.http.HttpServlet
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse

abstract class HttpBaseTest: HttpBaseKotlin() {

    companion object {
        @JvmStatic
        fun <T> testInServer(port: Int, path:String, holder: ServletHolder, targetFunc:(FuturePromise<T>)->Unit): T {

            val server = Server(port)
            val handler = ServletHandler()
            server.handler = handler
            handler.addServletWithMapping(holder, path)
            val fp = FuturePromise<T>()
            try {
                server.start()
                targetFunc(fp)
                return fp.get()
            } finally {
                server.stop()
            }
        }


        @JvmStatic
        fun <T> testPostInServer(port: Int, path:String, mockServiceFun:(HttpServletRequest, HttpServletResponse)->Unit, targetFunc:(FuturePromise<T>)->Unit): T {
            return testInServer(port, path, ServletHolder(object: HttpServlet() {
                override fun doPost(req: HttpServletRequest?, resp: HttpServletResponse?) {
                    if(req!=null&&resp!=null) {
                        mockServiceFun(req, resp)
                    }
                }
            }), targetFunc)
        }

        @JvmStatic
        fun <T> testGetInServer(port: Int, path:String, mockServiceFun:(HttpServletRequest, HttpServletResponse)->Unit, targetFunc:(FuturePromise<T>)->Unit): T {
            return testInServer(port, path, ServletHolder(object: HttpServlet() {
                override fun doGet(req: HttpServletRequest?, resp: HttpServletResponse?) {
                    if(req!=null&&resp!=null) {
                        mockServiceFun(req, resp)
                    }
                }
            }), targetFunc)
        }

        @JvmStatic
        fun <T> testPutInServer(port: Int, path:String, mockServiceFun:(HttpServletRequest, HttpServletResponse)->Unit, targetFunc:(FuturePromise<T>)->Unit): T {
            return testInServer(port, path, ServletHolder(object: HttpServlet() {
                override fun doPut(req: HttpServletRequest?, resp: HttpServletResponse?) {
                    if(req!=null&&resp!=null) {
                        mockServiceFun(req, resp)
                    }
                }
            }), targetFunc)
        }

        @JvmStatic
        fun <T> testDeleteInServer(port: Int, path:String, mockServiceFun:(HttpServletRequest, HttpServletResponse)->Unit, targetFunc:(FuturePromise<T>)->Unit): T {
            return testInServer(port, path, ServletHolder(object: HttpServlet() {
                override fun doDelete(req: HttpServletRequest?, resp: HttpServletResponse?) {
                    if(req!=null&&resp!=null) {
                        mockServiceFun(req, resp)
                    }
                }
            }), targetFunc)
        }

        @JvmStatic
        fun <T> testTraceInServer(port: Int, path:String, mockServiceFun:(HttpServletRequest, HttpServletResponse)->Unit, targetFunc:(FuturePromise<T>)->Unit): T {
            return testInServer(port, path, ServletHolder(object: HttpServlet() {
                override fun doTrace(req: HttpServletRequest?, resp: HttpServletResponse?) {
                    if(req!=null&&resp!=null) {
                        mockServiceFun(req, resp)
                    }
                }
            }), targetFunc)
        }

        @JvmStatic
        fun <T> testHeadInServer(port: Int, path:String, mockServiceFun:(HttpServletRequest, HttpServletResponse)->Unit, targetFunc:(FuturePromise<T>)->Unit): T {
            return testInServer(port, path, ServletHolder(object: HttpServlet() {
                override fun doHead(req: HttpServletRequest?, resp: HttpServletResponse?) {
                    if(req!=null&&resp!=null) {
                        mockServiceFun(req, resp)
                    }
                }
            }), targetFunc)
        }

        @JvmStatic
        fun <T> testOptionsInServer(port: Int, path:String, mockServiceFun:(HttpServletRequest, HttpServletResponse)->Unit, targetFunc:(FuturePromise<T>)->Unit): T {
            return testInServer(port, path, ServletHolder(object: HttpServlet() {
                override fun doOptions(req: HttpServletRequest?, resp: HttpServletResponse?) {
                    if(req!=null&&resp!=null) {
                        mockServiceFun(req, resp)
                    }
                }
            }), targetFunc)
        }
    }
}
