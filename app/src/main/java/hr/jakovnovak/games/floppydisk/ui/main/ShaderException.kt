package hr.jakovnovak.games.floppydisk.ui.main

import android.opengl.GLES20

class ShaderException(shaderId : Int, program : Boolean = false)
    : IllegalStateException(if(program) GLES20.glGetShaderInfoLog(shaderId) else GLES20.glGetProgramInfoLog(shaderId))