package hr.jakovnovak.games.floppydisk.ui.main

import android.content.Context
import android.content.res.Resources
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.PixelFormat
import android.opengl.EGL14
import android.opengl.GLES32
import android.opengl.GLSurfaceView
import android.util.AttributeSet
import android.util.Log
import hr.jakovnovak.games.floppydisk.R
import kotlinx.coroutines.processNextEventInCurrentThread
import java.nio.ByteBuffer
import java.nio.FloatBuffer
import java.nio.IntBuffer
import javax.microedition.khronos.egl.EGLConfig
import javax.microedition.khronos.opengles.GL10

class CustomLogoView(context : Context, attrs : AttributeSet?) : GLSurfaceView(context, attrs) {
    companion object RendererImpl : Renderer {
        private val vao = IntArray(1)
        private val vbo = IntArray(2) // za xy i uv koordinate
        private val textures = IntArray(6)
        private val textureBitmaps = mutableListOf<Bitmap>()
        private var program : Int = 0
        private lateinit var vertexSrc : String
        private lateinit var fragmentSrc : String
        private lateinit var res : Resources

        // parametri za liniju
        private val XWidth = 0.1f
        private val YHeight = 0.2f
        private var currX = -1f
        private var currY =  1f
        // trenutna tekstura
        private var currTex = 0

        private val xy_coords = floatArrayOf(
            -1f, -1f,
            -1f,  1f,
             1f, -1f,

            1f , -1f,
            1f ,  1f,
            -1f,  1f
        )
        private val uv_coords = floatArrayOf(
            0f, 1f,
            0f, 0f,
            1f, 1f,

            1f, 1f,
            1f, 0f,
            0f, 0f
        )

        private fun loadTextures() {
            val resIds = intArrayOf(R.drawable.floppy_disk, R.drawable.cd, R.drawable.computer_flipped,
                R.drawable.mouse, R.drawable.floppy_disk_red, R.drawable.floppy_disk_green)
            assert(resIds.size == textures.size)

            for (resId in resIds) {
                val texBitmap = BitmapFactory
                    .decodeResource(res, resId)
                    .copy(Bitmap.Config.ARGB_8888, false)

                textureBitmaps.add(texBitmap)
            }
        }

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

            GLES32.glUniform1f(GLES32.glGetUniformLocation(program, "XWidth"), XWidth)
            GLES32.glUniform1f(GLES32.glGetUniformLocation(program, "YHeight"), YHeight)
            GLES32.glUniform1i(GLES32.glGetUniformLocation(program, "texSampler"), 0)

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
            GLES32.glGenTextures(textures.size, textures, 0);

            for ((index, texBitmap) in textureBitmaps.withIndex()) {
                GLES32.glBindTexture(GLES32.GL_TEXTURE_2D, textures[index])

                val w = texBitmap.width
                val h = texBitmap.height
                val pixels = ByteBuffer.allocate(w * h * 4)
                texBitmap.copyPixelsToBuffer(pixels)
                pixels.rewind()
                GLES32.glTexImage2D(GLES32.GL_TEXTURE_2D, 0, GLES32.GL_RGBA, w, h, 0, GLES32.GL_RGBA, GLES32.GL_UNSIGNED_BYTE, pixels)
                GLES32.glGenerateMipmap(GLES32.GL_TEXTURE_2D)

                GLES32.glBindTexture(GLES32.GL_TEXTURE_2D, 0)
            }
        }

        override fun onSurfaceChanged(gl: GL10?, width: Int, height: Int) {
            GLES32.glViewport(0, 0, width, height)
            EGL14.eglSurfaceAttrib(EGL14.eglGetCurrentDisplay(),
                EGL14.eglGetCurrentSurface(EGL14.EGL_DRAW),
                EGL14.EGL_SWAP_BEHAVIOR,
                EGL14.EGL_BUFFER_PRESERVED);
        }

        override fun onDrawFrame(gl: GL10?) {
            GLES32.glUseProgram(program)

            GLES32.glUniform1f(GLES32.glGetUniformLocation(program, "currX"), currX)
            GLES32.glUniform1f(GLES32.glGetUniformLocation(program, "currY"), currY)
            if(currX + XWidth >= 1f && currY - YHeight <= -1.05f)
                GLES32.glUniform1i(GLES32.glGetUniformLocation(program, "lastFrame"), 1)
            else
                GLES32.glUniform1i(GLES32.glGetUniformLocation(program, "lastFrame"), 0)

            GLES32.glBindVertexArray(vao[0])
            GLES32.glActiveTexture(GLES32.GL_TEXTURE0)
            GLES32.glBindTexture(GLES32.GL_TEXTURE_2D, textures[currTex])
                GLES32.glDrawArrays(GLES32.GL_TRIANGLES, 0, xy_coords.size / 2)
            GLES32.glBindTexture(GLES32.GL_TEXTURE_2D, 0)
            GLES32.glBindVertexArray(0)

            currX += XWidth
            if(currX >= 1f) {
                currX = -1f
                currY -= YHeight
                if(currY <= -1f) {
                    currY = 1f
                    currTex = (currTex + 1) % textures.size
                }
            }

            Thread.sleep(30)
        }
    }

    init {
        vertexSrc = String(resources.openRawResource(R.raw.vertex).readAllBytes())
        fragmentSrc = String(resources.openRawResource(R.raw.fragment).readAllBytes())
        res = resources
        if(textureBitmaps.size == 0)
            loadTextures()

        setZOrderOnTop(true)
        holder.setFormat(PixelFormat.RGBA_8888)
        setEGLConfigChooser(8, 8, 8, 8, 16, 0)
        setEGLContextClientVersion(3)
        preserveEGLContextOnPause = true
        setRenderer(RendererImpl)
        renderMode = RENDERMODE_CONTINUOUSLY
    }
}