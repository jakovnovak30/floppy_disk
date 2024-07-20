package hr.jakovnovak.games.floppydisk.ui.main

import android.content.Context
import android.opengl.GLES20
import android.opengl.GLSurfaceView
import android.util.AttributeSet
import java.nio.FloatBuffer
import java.nio.IntBuffer
import javax.microedition.khronos.egl.EGLConfig
import javax.microedition.khronos.opengles.GL
import javax.microedition.khronos.opengles.GL10

class CustomLogoView(context : Context, attrs : AttributeSet?) : GLSurfaceView(context, attrs) {
    companion object RendererImpl : Renderer {
        private val vao = IntArray(1)
        private val vbo = IntArray(2) // za xy i uv koordinate
        private var program : Int = 0

        private val xy_coords = floatArrayOf(
            0f, 0f,
            0f, 1f,
            1f, 0f
        )
        private val uv_coords = intArrayOf(1, 2, 3)

        override fun onSurfaceCreated(gl: GL10?, config: EGLConfig?) {
            GLES20.glClearColor(0f, 0f, 0f, 0f)

            // initialize buffers
            GLES20.glGenBuffers(1, vao, 0)
            GLES20.glBindBuffer(GLES20.GL_ARRAY_BUFFER, vao[0])

                GLES20.glGenBuffers(2, vbo, 0)
                GLES20.glBindBuffer(GLES20.GL_ELEMENT_ARRAY_BUFFER, vbo[0])
                    val data1 = FloatBuffer.allocate(xy_coords.size)
                    data1.put(xy_coords)
                    GLES20.glBufferData(GLES20.GL_ARRAY_BUFFER, xy_coords.size * Int.SIZE_BYTES, data1, GLES20.GL_STATIC_DRAW)

                    GLES20.glVertexAttribPointer(0, 2, GLES20.GL_FLOAT, false, 2 * Float.SIZE_BYTES, 0)
                    GLES20.glEnableVertexAttribArray(0)
                GLES20.glBindBuffer(GLES20.GL_ELEMENT_ARRAY_BUFFER, 0)

                GLES20.glBindBuffer(GLES20.GL_ELEMENT_ARRAY_BUFFER, vbo[1])
                    val data2 = IntBuffer.allocate(uv_coords.size)
                    data2.put(uv_coords)
                    GLES20.glBufferData(GLES20.GL_ELEMENT_ARRAY_BUFFER, uv_coords.size * Int.SIZE_BYTES, data2, GLES20.GL_STATIC_DRAW)

                    GLES20.glVertexAttribPointer(1, 2, GLES20.GL_FLOAT, false, 2 * Float.SIZE_BYTES, 0)
                    GLES20.glEnableVertexAttribArray(1)
                GLES20.glBindBuffer(GLES20.GL_ELEMENT_ARRAY_BUFFER, 0)

            GLES20.glBindBuffer(GLES20.GL_ARRAY_BUFFER, 0)

            // initialize texture
            GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE1)
                // TODO: postavi teksturu
            GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE0)

            // compile shaders
            program = GLES20.glCreateProgram()
            GLES20.glShaderSource(GLES20.GL_VERTEX_SHADER, "src1")
            GLES20.glShaderSource(GLES20.GL_FRAGMENT_SHADER, "src2")

        }

        override fun onSurfaceChanged(gl: GL10?, width: Int, height: Int) {
            // TODO: slozi ovo kak spada
            GLES20.glViewport(0, 0, width, height)
        }

        override fun onDrawFrame(gl: GL10?) {
            GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT)

            GLES20.glActiveTexture(GLES20.GL_TEXTURE1)
            GLES20.glUseProgram(program)
            GLES20.glDrawArrays(GLES20.GL_TRIANGLES, 0, 0)

        }
    }

    init {
        setEGLContextClientVersion(2)
        setRenderer(RendererImpl)
    }
}