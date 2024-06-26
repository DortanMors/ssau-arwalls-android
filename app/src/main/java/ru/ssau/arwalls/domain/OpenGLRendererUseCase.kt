package ru.ssau.arwalls.domain

import android.content.Context
import android.opengl.GLES20
import android.opengl.GLSurfaceView
import android.util.Log
import com.google.ar.core.Session
import com.google.ar.core.TrackingState
import ru.ssau.arwalls.common.helpers.DisplayRotationHelper
import ru.ssau.arwalls.common.helpers.SnackBarUseCase
import ru.ssau.arwalls.common.rendering.BackgroundRenderer
import ru.ssau.arwalls.common.rendering.DepthRenderer
import ru.ssau.arwalls.common.tag
import ru.ssau.arwalls.data.PartialMapStore
import ru.ssau.arwalls.rawdepth.create
import java.io.IOException
import java.nio.FloatBuffer
import javax.microedition.khronos.egl.EGLConfig
import javax.microedition.khronos.opengles.GL10
import ru.ssau.arwalls.common.Settings.NoTrackingWarnings
import ru.ssau.arwalls.data.RawMapStore
import ru.ssau.arwalls.rawdepth.R

class OpenGLRendererUseCase(
    private val context: Context,
    private val displayRotationHelper: DisplayRotationHelper,
): GLSurfaceView.Renderer {

    private val backgroundRenderer = BackgroundRenderer()
    private val depthRenderer = DepthRenderer()
    var session: Session? = null

    override fun onSurfaceCreated(gl: GL10, config: EGLConfig) {
            GLES20.glClearColor(0.1f, 0.1f, 0.1f, 1.0f)

            // Prepare the rendering objects. This involves reading shaders, so may throw an IOException.
            try {
                // Create the texture and pass it to ARCore session to be filled during update().
                backgroundRenderer.createOnGlThread(context)
                depthRenderer.createOnGlThread(context)
            } catch (e: IOException) {
                Log.e(tag, "Failed to read an asset file", e)
            }
    }

    override fun onSurfaceChanged(gl: GL10, width: Int, height: Int) {
            displayRotationHelper.onSurfaceChanged(width, height)
            GLES20.glViewport(0, 0, width, height)
    }

    override fun onDrawFrame(gl: GL10) {
            // Clear screen to notify driver it should not load any pixels from previous frame.
            GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT or GLES20.GL_DEPTH_BUFFER_BIT)
            // Notify ARCore session that the view size changed so that the perspective matrix and
            // the video background can be properly adjusted.
            val currentSession = session ?: return
            try {
                displayRotationHelper.updateSessionIfNeeded(currentSession)
                currentSession.setCameraTextureName(backgroundRenderer.textureId)

                // Obtain the current frame from ARSession. When the configuration is set to
                // UpdateMode.BLOCKING (it is by default), this will throttle the rendering to the
                // camera framerate.
                val frame = currentSession.update()
                val camera = frame.camera

                // If frame is ready, render camera preview image to the GL surface.
                backgroundRenderer.draw(frame)

                // Retrieve the depth data for this frame.
                val mapState = create(
                    frame,
                    currentSession.createAnchor(camera.pose)
                ) ?: return
                val points: FloatBuffer = mapState.points
                depthRenderer.update(points)
                depthRenderer.draw(camera)
                SnackBarUseCase.hide(NoTrackingWarnings)

                // If not tracking, show tracking failure reason instead.
                if (camera.trackingState == TrackingState.PAUSED) {
                    SnackBarUseCase.showMessage(
                        messageId = R.string.no_tracking,
                    )
                    return
                }
                PartialMapStore.updateMapState(mapState)
                RawMapStore.updateMapStateAsync(mapState)
            } catch (t: Throwable) {
                Log.e(tag, "Exception on the OpenGL thread", t)
            }
    }
}
