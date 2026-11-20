/*
 * FloatClient-nextgen - A free auxiliary mod for Minecraft Java Edition.
 * Copyright (C) 2026  FloatingPoint-MC
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */

package io.github.floatingpointmc.client.nextgen.ui.shader;

import io.github.floatingpointmc.client.nextgen.util.io.IOUtil;
import lombok.extern.log4j.Log4j2;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.util.Objects;

import static org.lwjgl.opengl.GL20.*;

@Log4j2
public class Shader {
    private final int programId;

    public Shader(@NotNull String fragmentShader) {
        try {
            int program = glCreateProgram();
            int fragmentShaderID = createShader(
                    new String(
                            IOUtil.toByte(
                                    Objects.requireNonNull(
                                            Shader.class.getResourceAsStream(
                                                    "/assets/floatclient-nextgen/shaders/%s.frag".formatted(fragmentShader)
                                            )
                                    )
                            )
                    ),
                    GL_FRAGMENT_SHADER
            );
            glAttachShader(program, fragmentShaderID);
            int vertexShaderID = createShader(
                    new String(
                            IOUtil.toByte(
                                    Objects.requireNonNull(
                                            Shader.class.getResourceAsStream(
                                                    "/assets/floatclient-nextgen/shaders/shader.vert"
                                            )
                                    )
                            )
                    ),
                    GL_VERTEX_SHADER
            );
            glAttachShader(program, vertexShaderID);

            glLinkProgram(program);
            int status = glGetProgrami(program, GL_LINK_STATUS);

            if (status == 0) {
                throw new IllegalStateException("Shader failed to link!");
            }
            this.programId = program;
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void init() {
        glUseProgram(programId);
    }

    public void unload() {
        glUseProgram(0);
    }

    public void setUniformf(String name, float... args) {
        int loc = glGetUniformLocation(programId, name);
        switch (args.length) {
            case 1:
                glUniform1f(loc, args[0]);
                break;
            case 2:
                glUniform2f(loc, args[0], args[1]);
                break;
            case 3:
                glUniform3f(loc, args[0], args[1], args[2]);
                break;
            case 4:
                glUniform4f(loc, args[0], args[1], args[2], args[3]);
                break;
        }
    }

    public void setUniformi(String name, int... args) {
        int loc = glGetUniformLocation(programId, name);
        if (args.length > 1) glUniform2i(loc, args[0], args[1]);
        else glUniform1i(loc, args[0]);
    }

    public static void drawQuads(float x, float y, float width, float height) {
        glBegin(GL_QUADS);
        glTexCoord2f(0, 0);
        glVertex2f(x, y);
        glTexCoord2f(0, 1);
        glVertex2f(x, y + height);
        glTexCoord2f(1, 1);
        glVertex2f(x + width, y + height);
        glTexCoord2f(1, 0);
        glVertex2f(x + width, y);
        glEnd();
    }

    private int createShader(@NotNull String context, int type) {
        int shader = glCreateShader(type);
        glShaderSource(shader, context);
        glCompileShader(shader);

        if (glGetShaderi(shader, GL_COMPILE_STATUS) == 0) {
            log.error(glGetShaderInfoLog(shader, 4096));
            throw new IllegalStateException(String.format("Shader (%s) failed to compile!", type));
        }

        return shader;
    }
}
