package hr.jakovnovak.games.floppydisk.ui.main

import android.content.Context
import android.content.res.Resources
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.PixelFormat
import android.opengl.GLES32
import android.opengl.GLSurfaceView
import android.util.AttributeSet
import android.util.Log
import hr.jakovnovak.games.floppydisk.R
import java.nio.ByteBuffer
import java.nio.FloatBuffer
import java.nio.IntBuffer
import javax.microedition.khronos.egl.EGLConfig
import javax.microedition.khronos.opengles.GL10

class CustomLogoView(context : Context, attrs : AttributeSet?) : GLSurfaceView(context, attrs) {
    companion object RendererImpl : Renderer {
        private val vao = IntArray(1)
        private val vbo = IntArray(2) // za xy i uv koordinate
        private val texture = IntArray(1)
        private var program : Int = 0
        private lateinit var vertexSrc : String
        private lateinit var fragmentSrc : String
        private lateinit var res : Resources

        private val xy_coords = floatArrayOf(
            -1f, -1f,
            -1f,  1f,
             1f, -1f,

            1f , -1f,
            1f ,  1f,
            -1f,  1f
        )
        private val uv_coords = floatArrayOf(
            0f, 0f,
            0f, 1f,
            1f, 0f,

            1f, 0f,
            1f, 1f,
            0f, 1f
        )

        override fun onSurfaceCreated(gl: GL10?, config: EGLConfig?) {
            GLES32.glClearColor(0f, 0f, 0f, 0f)

            // compile shaders
            program = GLES32.glCreateProgram()

            val successBuffer = IntBuffer.allocate(1)

            val vertex = GLES32.glCreateShader(GLES32.GL_VERTEX_SHADER)
            GLES32.glShaderSource(vertex, vertexSrc)
            GLES32.glCompileShader(vertex)
            GLES32.glGetShaderiv(vertex, GLES32.GL_COMPILE_STATUS, successBuffer)

            //Log.d("shader", "Greska je: " + GLES32.glGetShaderInfoLog(vertex))
            if(successBuffer.get(0) == 0)
                throw ShaderException(vertex)

            val fragment = GLES32.glCreateShader(GLES32.GL_FRAGMENT_SHADER)
            GLES32.glShaderSource(fragment, fragmentSrc)
            GLES32.glCompileShader(fragment)
            GLES32.glGetShaderiv(fragment, GLES32.GL_COMPILE_STATUS, successBuffer)

            Log.d("shader", "Greska je: " + GLES32.glGetShaderInfoLog(fragment))
            if(successBuffer.get(0) == 0)
                throw ShaderException(fragment)

            GLES32.glAttachShader(program, vertex)
            GLES32.glAttachShader(program, fragment)

            GLES32.glBindAttribLocation(program, 0, "pos")
            GLES32.glBindAttribLocation(program, 1, "uv_coord")
            GLES32.glLinkProgram(program)

            GLES32.glGetProgramiv(program, GLES32.GL_LINK_STATUS, successBuffer)

            if(successBuffer.get(0) == 0)
                throw ShaderException(program, true)

            GLES32.glDeleteShader(vertex)
            GLES32.glDeleteShader(fragment)
            GLES32.glUseProgram(program)

            // initialize buffers
            GLES32.glGenVertexArrays(1, vao, 0)
            GLES32.glBindVertexArray(vao[0])

                GLES32.glGenBuffers(2, vbo, 0)
                GLES32.glBindBuffer(GLES32.GL_ARRAY_BUFFER, vbo[0])
                    val data1 = FloatBuffer.allocate(xy_coords.size)
                        .put(xy_coords).rewind()
                    GLES32.glBufferData(GLES32.GL_ARRAY_BUFFER, xy_coords.size * Float.SIZE_BYTES, data1, GLES32.GL_STATIC_DRAW)

                    GLES32.glVertexAttribPointer(0, 2, GLES32.GL_FLOAT, false, 2 * Float.SIZE_BYTES, 0)
                    GLES32.glEnableVertexAttribArray(0)
                GLES32.glBindBuffer(GLES32.GL_ARRAY_BUFFER, 0)

                GLES32.glBindBuffer(GLES32.GL_ARRAY_BUFFER, vbo[1])
                    val data2 = FloatBuffer.allocate(uv_coords.size)
                        .put(uv_coords).rewind()
                    GLES32.glBufferData(GLES32.GL_ARRAY_BUFFER, uv_coords.size * Float.SIZE_BYTES, data2, GLES32.GL_STATIC_DRAW)

                    GLES32.glVertexAttribPointer(1, 2, GLES32.GL_FLOAT, false, 2 * Float.SIZE_BYTES, 0)
                    GLES32.glEnableVertexAttribArray(1)
                GLES32.glBindBuffer(GLES32.GL_ARRAY_BUFFER, 0)

            GLES32.glBindVertexArray(0)

            // initialize texture
            GLES32.glGenTextures(1, texture, 0);
            GLES32.glActiveTexture(GLES32.GL_TEXTURE0)
            GLES32.glBindTexture(GLES32.GL_TEXTURE_2D, texture[0])

                val texBitmap = BitmapFactory
                    .decodeResource(res, R.drawable.floppy_disk)
                    .copy(Bitmap.Config.ARGB_8888, false)
                val w = texBitmap.width
                val h = texBitmap.height
                val pixels = ByteBuffer.allocate(w * h * 4)
                texBitmap.copyPixelsToBuffer(pixels)
                pixels.rewind()
                GLES32.glTexImage2D(GLES32.GL_TEXTURE_2D, 0, GLES32.GL_RGBA, w, h, 0, GLES32.GL_RGBA, GLES32.GL_UNSIGNED_BYTE, pixels)
                GLES32.glGenerateMipmap(GLES32.GL_TEXTURE_2D)

                GLES32.glUniform1i(GLES32.glGetUniformLocation(program, "texSampler"), 0)
            GLES32.glBindTexture(GLES32.GL_TEXTURE_2D, 0)
        }

        override fun onSurfaceChanged(gl: GL10?, width: Int, height: Int) {
            // TODO: slozi ovo kak spada
            GLES32.glViewport(0, 0, width, height)
        }

        override fun onDrawFrame(gl: GL10?) {
            GLES32.glClear(GLES32.GL_COLOR_BUFFER_BIT or GLES32.GL_DEPTH_BUFFER_BIT)

            GLES32.glUseProgram(program)

            GLES32.glUniform1f(GLES32.glGetUniformLocation(program, "time"), (System.currentTimeMillis() % 60).toFloat() / 60 * 2 - 1)
            GLES32.glBindVertexArray(vao[0])
            GLES32.glActiveTexture(GLES32.GL_TEXTURE0)
            GLES32.glBindTexture(GLES32.GL_TEXTURE_2D, texture[0])
                GLES32.glDrawArrays(GLES32.GL_TRIANGLES, 0, xy_coords.size / 2)
            GLES32.glBindTexture(GLES32.GL_TEXTURE_2D, 0)
            GLES32.glBindVertexArray(0)

            Thread.sleep(100)
        }
    }

    init {
        vertexSrc = String(resources.openRawResource(R.raw.vertex).readAllBytes())
        fragmentSrc = String(resources.openRawResource(R.raw.fragment).readAllBytes())
        res = resources

        setZOrderOnTop(true)
        holder.setFormat(PixelFormat.RGBA_8888)
        setEGLConfigChooser(8, 8, 8, 8, 16, 0)
        setEGLContextClientVersion(3)
        setRenderer(RendererImpl)
        renderMode = RENDERMODE_CONTINUOUSLY
    }
}