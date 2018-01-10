/*
 * This file is part of LanternServer, licensed under the MIT License (MIT).
 *
 * Copyright (c) LanternPowered <https://www.lanternpowered.org>
 * Copyright (c) SpongePowered <https://www.spongepowered.org>
 * Copyright (c) contributors
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the Software), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED AS IS, WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */
package org.lanternpowered.server.data.persistence;

import com.flowpowered.math.imaginary.Complexd;
import com.flowpowered.math.imaginary.Complexf;
import com.flowpowered.math.imaginary.Quaterniond;
import com.flowpowered.math.imaginary.Quaternionf;
import com.flowpowered.math.vector.Vector2d;
import com.flowpowered.math.vector.Vector2f;
import com.flowpowered.math.vector.Vector2i;
import com.flowpowered.math.vector.Vector2l;
import com.flowpowered.math.vector.Vector3d;
import com.flowpowered.math.vector.Vector3f;
import com.flowpowered.math.vector.Vector3i;
import com.flowpowered.math.vector.Vector3l;
import com.flowpowered.math.vector.Vector4d;
import com.flowpowered.math.vector.Vector4f;
import com.flowpowered.math.vector.Vector4i;
import com.flowpowered.math.vector.Vector4l;
import com.google.common.reflect.TypeToken;
import org.lanternpowered.server.data.DataQueries;
import org.spongepowered.api.data.DataContainer;
import org.spongepowered.api.data.DataManager;
import org.spongepowered.api.data.DataQuery;
import org.spongepowered.api.data.DataView;
import org.spongepowered.api.data.Queries;
import org.spongepowered.api.data.persistence.DataTranslator;
import org.spongepowered.api.data.persistence.InvalidDataException;

import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.Month;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.time.temporal.ChronoField;
import java.util.UUID;
import java.util.function.Supplier;

public final class DataTranslators {

    public static final DataTranslator<UUID> UUID_DATA_SERIALIZER;
    public static final DataTranslator<Vector2d> VECTOR_2_D_DATA_SERIALIZER;
    public static final DataTranslator<Vector2f> VECTOR_2_F_DATA_SERIALIZER;
    public static final DataTranslator<Vector2i> VECTOR_2_I_DATA_SERIALIZER;
    public static final DataTranslator<Vector2l> VECTOR_2_L_DATA_SERIALIZER;
    public static final DataTranslator<Vector3d> VECTOR_3_D_DATA_SERIALIZER;
    public static final DataTranslator<Vector3f> VECTOR_3_F_DATA_SERIALIZER;
    public static final DataTranslator<Vector3i> VECTOR_3_I_DATA_SERIALIZER;
    public static final DataTranslator<Vector3l> VECTOR_3_L_DATA_SERIALIZER;
    public static final DataTranslator<Vector4d> VECTOR_4_D_DATA_SERIALIZER;
    public static final DataTranslator<Vector4f> VECTOR_4_F_DATA_SERIALIZER;
    public static final DataTranslator<Vector4i> VECTOR_4_I_DATA_SERIALIZER;
    public static final DataTranslator<Vector4l> VECTOR_4_L_DATA_SERIALIZER;
    public static final DataTranslator<Complexd> COMPLEXD_DATA_SERIALIZER;
    public static final DataTranslator<Complexf> COMPLEXF_DATA_SERIALIZER;
    public static final DataTranslator<Quaterniond> QUATERNIOND_DATA_SERIALIZER;
    public static final DataTranslator<Quaternionf> QUATERNIONF_DATA_SERIALIZER;
    public static final DataTranslator<LocalTime> LOCAL_TIME_DATA_SERIALIZER;
    public static final DataTranslator<LocalDate> LOCAL_DATE_DATA_SERIALIZER;
    public static final DataTranslator<LocalDateTime> LOCAL_DATE_TIME_DATA_SERIALIZER;
    public static final DataTranslator<Instant> INSTANT_DATA_SERIALIZER;
    public static final DataTranslator<ZonedDateTime> ZONED_DATE_TIME_DATA_SERIALIZER;
    public static final DataTranslator<Month> MONTH_DATA_SERIALIZER;

    static {
        UUID_DATA_SERIALIZER = new AbstractDataTranslator<UUID>(
                "sponge", "uuid", "UuidTranslator", TypeToken.of(UUID.class)) {

            @Override
            public UUID translate(DataView view) throws InvalidDataException {
                final long least = view.getLong(Queries.UUID_LEAST).orElseThrow(invalidDataQuery(Queries.UUID_LEAST));
                final long most = view.getLong(Queries.UUID_MOST).orElseThrow(invalidDataQuery(Queries.UUID_MOST));
                return new UUID(most, least);
            }

            @Override
            public DataContainer translate(UUID obj) throws InvalidDataException {
                return DataContainer.createNew()
                        .set(Queries.UUID_LEAST, obj.getLeastSignificantBits())
                        .set(Queries.UUID_MOST, obj.getMostSignificantBits());
            }
        };
        VECTOR_2_D_DATA_SERIALIZER = new AbstractDataTranslator<Vector2d>(
                "sponge", "vector_2_d", "Vector2dTranslator", TypeToken.of(Vector2d.class)) {

            @Override
            public Vector2d translate(DataView view) throws InvalidDataException {
                final double x = view.getDouble(DataQueries.X_POS).orElseThrow(invalidDataQuery(DataQueries.X_POS));
                final double y = view.getDouble(DataQueries.Y_POS).orElseThrow(invalidDataQuery(DataQueries.Y_POS));
                return new Vector2d(x, y);
            }

            @Override
            public DataContainer translate(Vector2d obj) throws InvalidDataException {
                return DataContainer.createNew()
                        .set(DataQueries.X_POS, obj.getX())
                        .set(DataQueries.Y_POS, obj.getY());
            }
        };
        VECTOR_2_F_DATA_SERIALIZER = new AbstractDataTranslator<Vector2f>(
                "sponge", "vector_2_f", "Vector2fTranslator", TypeToken.of(Vector2f.class)) {

            @Override
            public Vector2f translate(DataView view) throws InvalidDataException {
                final double x = view.getDouble(DataQueries.X_POS).orElseThrow(invalidDataQuery(DataQueries.X_POS));
                final double y = view.getDouble(DataQueries.Y_POS).orElseThrow(invalidDataQuery(DataQueries.Y_POS));
                return new Vector2f(x, y);
            }

            @Override
            public DataContainer translate(Vector2f obj) throws InvalidDataException {
                return DataContainer.createNew()
                        .set(DataQueries.X_POS, obj.getX())
                        .set(DataQueries.Y_POS, obj.getY());
            }
        };
        VECTOR_2_I_DATA_SERIALIZER = new AbstractDataTranslator<Vector2i>(
                "sponge", "vector_2_i", "Vector2iTranslator", TypeToken.of(Vector2i.class)) {

            @Override
            public Vector2i translate(DataView view) throws InvalidDataException {
                final int x = view.getInt(DataQueries.X_POS).orElseThrow(invalidDataQuery(DataQueries.X_POS));
                final int y = view.getInt(DataQueries.Y_POS).orElseThrow(invalidDataQuery(DataQueries.Y_POS));
                return new Vector2i(x, y);
            }

            @Override
            public DataContainer translate(Vector2i obj) throws InvalidDataException {
                return DataContainer.createNew()
                        .set(DataQueries.X_POS, obj.getX())
                        .set(DataQueries.Y_POS, obj.getY());
            }
        };
        VECTOR_2_L_DATA_SERIALIZER = new AbstractDataTranslator<Vector2l>(
                "sponge", "vector_2_l", "Vector2lTranslator", TypeToken.of(Vector2l.class)) {

            @Override
            public Vector2l translate(DataView view) throws InvalidDataException {
                final long x = view.getLong(DataQueries.X_POS).orElseThrow(invalidDataQuery(DataQueries.X_POS));
                final long y = view.getLong(DataQueries.Y_POS).orElseThrow(invalidDataQuery(DataQueries.Y_POS));
                return new Vector2l(x, y);
            }

            @Override
            public DataContainer translate(Vector2l obj) throws InvalidDataException {
                return DataContainer.createNew()
                        .set(DataQueries.X_POS, obj.getX())
                        .set(DataQueries.Y_POS, obj.getY());
            }
        };
        VECTOR_3_D_DATA_SERIALIZER = new AbstractDataTranslator<Vector3d>(
                "sponge", "vector_3_d", "Vector3dTranslator", TypeToken.of(Vector3d.class)) {

            @Override
            public Vector3d translate(DataView view) throws InvalidDataException {
                final double x = view.getDouble(DataQueries.X_POS).orElseThrow(invalidDataQuery(DataQueries.X_POS));
                final double y = view.getDouble(DataQueries.Y_POS).orElseThrow(invalidDataQuery(DataQueries.Y_POS));
                final double z = view.getDouble(DataQueries.Z_POS).orElseThrow(invalidDataQuery(DataQueries.Z_POS));
                return new Vector3d(x, y, z);
            }

            @Override
            public DataContainer translate(Vector3d obj) throws InvalidDataException {
                return DataContainer.createNew()
                        .set(DataQueries.X_POS, obj.getX())
                        .set(DataQueries.Y_POS, obj.getY())
                        .set(DataQueries.Z_POS, obj.getZ());
            }
        };
        VECTOR_3_F_DATA_SERIALIZER = new AbstractDataTranslator<Vector3f>(
                "sponge", "vector_3_f", "Vector3fTranslator", TypeToken.of(Vector3f.class)) {

            @Override
            public Vector3f translate(DataView view) throws InvalidDataException {
                final double x = view.getDouble(DataQueries.X_POS).orElseThrow(invalidDataQuery(DataQueries.X_POS));
                final double y = view.getDouble(DataQueries.Y_POS).orElseThrow(invalidDataQuery(DataQueries.Y_POS));
                final double z = view.getDouble(DataQueries.Z_POS).orElseThrow(invalidDataQuery(DataQueries.Z_POS));
                return new Vector3f(x, y, z);
            }

            @Override
            public DataContainer translate(Vector3f obj) throws InvalidDataException {
                return DataContainer.createNew()
                        .set(DataQueries.X_POS, obj.getX())
                        .set(DataQueries.Y_POS, obj.getY())
                        .set(DataQueries.Z_POS, obj.getZ());
            }
        };
        VECTOR_3_I_DATA_SERIALIZER = new AbstractDataTranslator<Vector3i>(
                "sponge", "vector_3_i", "Vector3iTranslator", TypeToken.of(Vector3i.class)) {

            @Override
            public Vector3i translate(DataView view) throws InvalidDataException {
                final int x = view.getInt(DataQueries.X_POS).orElseThrow(invalidDataQuery(DataQueries.X_POS));
                final int y = view.getInt(DataQueries.Y_POS).orElseThrow(invalidDataQuery(DataQueries.Y_POS));
                final int z = view.getInt(DataQueries.Z_POS).orElseThrow(invalidDataQuery(DataQueries.Z_POS));
                return new Vector3i(x, y, z);
            }

            @Override
            public DataContainer translate(Vector3i obj) throws InvalidDataException {
                return DataContainer.createNew()
                        .set(DataQueries.X_POS, obj.getX())
                        .set(DataQueries.Y_POS, obj.getY())
                        .set(DataQueries.Z_POS, obj.getZ());
            }
        };
        VECTOR_3_L_DATA_SERIALIZER = new AbstractDataTranslator<Vector3l>(
                "sponge", "vector_3_l", "Vector3lTranslator", TypeToken.of(Vector3l.class)) {

            @Override
            public Vector3l translate(DataView view) throws InvalidDataException {
                final long x = view.getLong(DataQueries.X_POS).orElseThrow(invalidDataQuery(DataQueries.X_POS));
                final long y = view.getLong(DataQueries.Y_POS).orElseThrow(invalidDataQuery(DataQueries.Y_POS));
                final long z = view.getLong(DataQueries.Z_POS).orElseThrow(invalidDataQuery(DataQueries.Z_POS));
                return new Vector3l(x, y, z);
            }

            @Override
            public DataContainer translate(Vector3l obj) throws InvalidDataException {
                return DataContainer.createNew()
                        .set(DataQueries.X_POS, obj.getX())
                        .set(DataQueries.Y_POS, obj.getY())
                        .set(DataQueries.Z_POS, obj.getZ());
            }
        };
        VECTOR_4_F_DATA_SERIALIZER = new AbstractDataTranslator<Vector4f>(
                "sponge", "vector_4_f", "Vector4fTranslator", TypeToken.of(Vector4f.class)) {

            @Override
            public Vector4f translate(DataView view) throws InvalidDataException {
                final double x = view.getDouble(DataQueries.X_POS).orElseThrow(invalidDataQuery(DataQueries.X_POS));
                final double y = view.getDouble(DataQueries.Y_POS).orElseThrow(invalidDataQuery(DataQueries.Y_POS));
                final double z = view.getDouble(DataQueries.Z_POS).orElseThrow(invalidDataQuery(DataQueries.Z_POS));
                final double w = view.getDouble(DataQueries.W_POS).orElseThrow(invalidDataQuery(DataQueries.W_POS));
                return new Vector4f(x, y, z, w);
            }

            @Override
            public DataContainer translate(Vector4f obj) throws InvalidDataException {
                return DataContainer.createNew()
                        .set(DataQueries.X_POS, obj.getX())
                        .set(DataQueries.Y_POS, obj.getY())
                        .set(DataQueries.Z_POS, obj.getZ())
                        .set(DataQueries.W_POS, obj.getW());
            }
        };
        VECTOR_4_I_DATA_SERIALIZER = new AbstractDataTranslator<Vector4i>(
                "sponge", "vector_4_i", "Vector4iTranslator", TypeToken.of(Vector4i.class)) {

            @Override
            public Vector4i translate(DataView view) throws InvalidDataException {
                final int x = view.getInt(DataQueries.X_POS).orElseThrow(invalidDataQuery(DataQueries.X_POS));
                final int y = view.getInt(DataQueries.Y_POS).orElseThrow(invalidDataQuery(DataQueries.Y_POS));
                final int z = view.getInt(DataQueries.Z_POS).orElseThrow(invalidDataQuery(DataQueries.Z_POS));
                final int w = view.getInt(DataQueries.W_POS).orElseThrow(invalidDataQuery(DataQueries.W_POS));
                return new Vector4i(x, y, z, w);
            }

            @Override
            public DataContainer translate(Vector4i obj) throws InvalidDataException {
                return DataContainer.createNew()
                        .set(DataQueries.X_POS, obj.getX())
                        .set(DataQueries.Y_POS, obj.getY())
                        .set(DataQueries.Z_POS, obj.getZ())
                        .set(DataQueries.W_POS, obj.getW());
            }
        };
        VECTOR_4_L_DATA_SERIALIZER = new AbstractDataTranslator<Vector4l>(
                "sponge", "vector_4_l", "Vector4lTranslator", TypeToken.of(Vector4l.class)) {

            @Override
            public Vector4l translate(DataView view) throws InvalidDataException {
                final long x = view.getLong(DataQueries.X_POS).orElseThrow(invalidDataQuery(DataQueries.X_POS));
                final long y = view.getLong(DataQueries.Y_POS).orElseThrow(invalidDataQuery(DataQueries.Y_POS));
                final long z = view.getLong(DataQueries.Z_POS).orElseThrow(invalidDataQuery(DataQueries.Z_POS));
                final long w = view.getLong(DataQueries.W_POS).orElseThrow(invalidDataQuery(DataQueries.W_POS));
                return new Vector4l(x, y, z, w);
            }

            @Override
            public DataContainer translate(Vector4l obj) throws InvalidDataException {
                return DataContainer.createNew()
                        .set(DataQueries.X_POS, obj.getX())
                        .set(DataQueries.Y_POS, obj.getY())
                        .set(DataQueries.Z_POS, obj.getZ())
                        .set(DataQueries.W_POS, obj.getW());
            }
        };
        VECTOR_4_D_DATA_SERIALIZER = new AbstractDataTranslator<Vector4d>(
                "sponge", "vector_4_d", "Vector4dTranslator", TypeToken.of(Vector4d.class)) {

            @Override
            public Vector4d translate(DataView view) throws InvalidDataException {
                final double x = view.getDouble(DataQueries.X_POS).orElseThrow(invalidDataQuery(DataQueries.X_POS));
                final double y = view.getDouble(DataQueries.Y_POS).orElseThrow(invalidDataQuery(DataQueries.Y_POS));
                final double z = view.getDouble(DataQueries.Z_POS).orElseThrow(invalidDataQuery(DataQueries.Z_POS));
                final double w = view.getDouble(DataQueries.W_POS).orElseThrow(invalidDataQuery(DataQueries.W_POS));
                return new Vector4d(x, y, z, w);
            }

            @Override
            public DataContainer translate(Vector4d obj) throws InvalidDataException {
                return DataContainer.createNew()
                        .set(DataQueries.X_POS, obj.getX())
                        .set(DataQueries.Y_POS, obj.getY())
                        .set(DataQueries.Z_POS, obj.getZ())
                        .set(DataQueries.W_POS, obj.getW());
            }
        };
        COMPLEXD_DATA_SERIALIZER = new AbstractDataTranslator<Complexd>(
                "sponge", "complex_d", "ComplexdTranslator", TypeToken.of(Complexd.class)) {

            @Override
            public Complexd translate(DataView view) throws InvalidDataException {
                final double x = view.getDouble(DataQueries.X_POS).orElseThrow(invalidDataQuery(DataQueries.X_POS));
                final double y = view.getDouble(DataQueries.Y_POS).orElseThrow(invalidDataQuery(DataQueries.Y_POS));
                return new Complexd(x, y);
            }

            @Override
            public DataContainer translate(Complexd obj) throws InvalidDataException {
                return DataContainer.createNew()
                        .set(DataQueries.X_POS, obj.getX())
                        .set(DataQueries.Y_POS, obj.getY());
            }
        };
        COMPLEXF_DATA_SERIALIZER = new AbstractDataTranslator<Complexf>(
                "sponge", "complex_f", "ComplexfTranslator", TypeToken.of(Complexf.class)) {

            @Override
            public Complexf translate(DataView view) throws InvalidDataException {
                final double x = view.getDouble(DataQueries.X_POS).orElseThrow(invalidDataQuery(DataQueries.X_POS));
                final double y = view.getDouble(DataQueries.Y_POS).orElseThrow(invalidDataQuery(DataQueries.Y_POS));
                return new Complexf(x, y);
            }

            @Override
            public DataContainer translate(Complexf obj) throws InvalidDataException {
                return DataContainer.createNew()
                        .set(DataQueries.X_POS, obj.getX())
                        .set(DataQueries.Y_POS, obj.getY());
            }
        };
        QUATERNIOND_DATA_SERIALIZER = new AbstractDataTranslator<Quaterniond>(
                "sponge", "quaternion_d", "QuaterniondTranslator", TypeToken.of(Quaterniond.class)) {

            @Override
            public Quaterniond translate(DataView view) throws InvalidDataException {
                final double x = view.getDouble(DataQueries.X_POS).orElseThrow(invalidDataQuery(DataQueries.X_POS));
                final double y = view.getDouble(DataQueries.Y_POS).orElseThrow(invalidDataQuery(DataQueries.Y_POS));
                final double z = view.getDouble(DataQueries.Z_POS).orElseThrow(invalidDataQuery(DataQueries.Z_POS));
                final double w = view.getDouble(DataQueries.W_POS).orElseThrow(invalidDataQuery(DataQueries.W_POS));
                return new Quaterniond(x, y, z, w);
            }

            @Override
            public DataContainer translate(Quaterniond obj) throws InvalidDataException {
                return DataContainer.createNew()
                        .set(DataQueries.X_POS, obj.getX())
                        .set(DataQueries.Y_POS, obj.getY())
                        .set(DataQueries.Z_POS, obj.getZ())
                        .set(DataQueries.W_POS, obj.getW());
            }
        };
        QUATERNIONF_DATA_SERIALIZER = new AbstractDataTranslator<Quaternionf>(
                "sponge", "quaternion_f", "QuaternionfTranslator", TypeToken.of(Quaternionf.class)) {

            @Override
            public Quaternionf translate(DataView view) throws InvalidDataException {
                final double x = view.getDouble(DataQueries.X_POS).orElseThrow(invalidDataQuery(DataQueries.X_POS));
                final double y = view.getDouble(DataQueries.Y_POS).orElseThrow(invalidDataQuery(DataQueries.Y_POS));
                final double z = view.getDouble(DataQueries.Z_POS).orElseThrow(invalidDataQuery(DataQueries.Z_POS));
                final double w = view.getDouble(DataQueries.W_POS).orElseThrow(invalidDataQuery(DataQueries.W_POS));
                return new Quaternionf(x, y, z, w);
            }

            @Override
            public DataContainer translate(Quaternionf obj) throws InvalidDataException {
                return DataContainer.createNew()
                        .set(DataQueries.X_POS, obj.getX())
                        .set(DataQueries.Y_POS, obj.getY())
                        .set(DataQueries.Z_POS, obj.getZ())
                        .set(DataQueries.W_POS, obj.getW());
            }
        };
        LOCAL_TIME_DATA_SERIALIZER = new AbstractDataTranslator<LocalTime>(
                "sponge", "local_time", "LocalTimeTranslator", TypeToken.of(LocalTime.class)) {

            @Override
            public LocalTime translate(DataView view) throws InvalidDataException {
                final int hour = view.getInt(DataQueries.LOCAL_TIME_HOUR).orElseThrow(invalidDataQuery(DataQueries.LOCAL_TIME_HOUR));
                final int minute = view.getInt(DataQueries.LOCAL_TIME_MINUTE).orElseThrow(invalidDataQuery(DataQueries.LOCAL_TIME_MINUTE));
                final int second = view.getInt(DataQueries.LOCAL_TIME_SECOND).orElseThrow(invalidDataQuery(DataQueries.LOCAL_TIME_SECOND));
                final int nano = view.getInt(DataQueries.LOCAL_TIME_NANO).orElseThrow(invalidDataQuery(DataQueries.LOCAL_TIME_NANO));
                if (!ChronoField.HOUR_OF_DAY.range().isValidValue(hour)) {
                    throw new InvalidDataException("Invalid hour of day: " + hour);
                }
                if (!ChronoField.MINUTE_OF_HOUR.range().isValidValue(minute)) {
                    throw new InvalidDataException("Invalid minute of hour: " + minute);
                }
                if (!ChronoField.SECOND_OF_MINUTE.range().isValidValue(second)) {
                    throw new InvalidDataException("Invalid second of minute: " + second);
                }
                if (!ChronoField.NANO_OF_SECOND.range().isValidValue(nano)) {
                    throw new InvalidDataException("Invalid nanosecond of second: " + nano);
                }
                return LocalTime.of(hour, minute, second, nano);
            }

            @Override
            public DataContainer translate(LocalTime obj) throws InvalidDataException {
                return DataContainer.createNew()
                        .set(DataQueries.LOCAL_TIME_HOUR, obj.getHour())
                        .set(DataQueries.LOCAL_TIME_MINUTE, obj.getMinute())
                        .set(DataQueries.LOCAL_TIME_SECOND, obj.getSecond())
                        .set(DataQueries.LOCAL_TIME_NANO, obj.getNano());
            }
        };
        LOCAL_DATE_DATA_SERIALIZER = new AbstractDataTranslator<LocalDate>(
                "sponge", "local_date", "LocalDateTranslator", TypeToken.of(LocalDate.class)) {

            @Override
            public LocalDate translate(DataView view) throws InvalidDataException {
                final int year = view.getInt(DataQueries.LOCAL_DATE_YEAR).orElseThrow(invalidDataQuery(DataQueries.LOCAL_DATE_YEAR));
                final int month = view.getInt(DataQueries.LOCAL_DATE_MONTH).orElseThrow(invalidDataQuery(DataQueries.LOCAL_DATE_MONTH));
                final int day = view.getInt(DataQueries.LOCAL_DATE_DAY).orElseThrow(invalidDataQuery(DataQueries.LOCAL_DATE_DAY));
                if (!ChronoField.YEAR.range().isValidValue(year)) {
                    throw new InvalidDataException("Invalid year: " + year);
                }
                if (!ChronoField.MONTH_OF_YEAR.range().isValidValue(month)) {
                    throw new InvalidDataException("Invalid month of year: " + month);
                }
                if (!ChronoField.DAY_OF_MONTH.range().isValidValue(day)) {
                    throw new InvalidDataException("Invalid day of month: " + day);
                }
                return LocalDate.of(year, month, day);
            }

            @Override
            public DataContainer translate(LocalDate obj) throws InvalidDataException {
                return DataContainer.createNew()
                        .set(DataQueries.LOCAL_DATE_YEAR, obj.getYear())
                        .set(DataQueries.LOCAL_DATE_MONTH, obj.getMonth())
                        .set(DataQueries.LOCAL_DATE_DAY, obj.getDayOfMonth());
            }
        };
        LOCAL_DATE_TIME_DATA_SERIALIZER = new AbstractDataTranslator<LocalDateTime>(
                "sponge", "local_date_time", "LocalDateTimeTranslator", TypeToken.of(LocalDateTime.class)) {

            @Override
            public LocalDateTime translate(DataView view) throws InvalidDataException {
                final int year = view.getInt(DataQueries.LOCAL_DATE_YEAR).orElseThrow(invalidDataQuery(DataQueries.LOCAL_DATE_YEAR));
                final int month = view.getInt(DataQueries.LOCAL_DATE_MONTH).orElseThrow(invalidDataQuery(DataQueries.LOCAL_DATE_MONTH));
                final int day = view.getInt(DataQueries.LOCAL_DATE_DAY).orElseThrow(invalidDataQuery(DataQueries.LOCAL_DATE_DAY));
                final int hour = view.getInt(DataQueries.LOCAL_TIME_HOUR).orElseThrow(invalidDataQuery(DataQueries.LOCAL_TIME_HOUR));
                final int minute = view.getInt(DataQueries.LOCAL_TIME_MINUTE).orElseThrow(invalidDataQuery(DataQueries.LOCAL_TIME_MINUTE));
                final int second = view.getInt(DataQueries.LOCAL_TIME_SECOND).orElseThrow(invalidDataQuery(DataQueries.LOCAL_TIME_SECOND));
                final int nano = view.getInt(DataQueries.LOCAL_TIME_NANO).orElseThrow(invalidDataQuery(DataQueries.LOCAL_TIME_NANO));
                if (!ChronoField.YEAR.range().isValidValue(year)) {
                    throw new InvalidDataException("Invalid year: " + year);
                }
                if (!ChronoField.MONTH_OF_YEAR.range().isValidValue(month)) {
                    throw new InvalidDataException("Invalid month of year: " + month);
                }
                if (!ChronoField.DAY_OF_MONTH.range().isValidValue(day)) {
                    throw new InvalidDataException("Invalid day of month: " + day);
                }
                if (!ChronoField.HOUR_OF_DAY.range().isValidValue(hour)) {
                    throw new InvalidDataException("Invalid hour of day: " + hour);
                }
                if (!ChronoField.MINUTE_OF_HOUR.range().isValidValue(minute)) {
                    throw new InvalidDataException("Invalid minute of hour: " + minute);
                }
                if (!ChronoField.SECOND_OF_MINUTE.range().isValidValue(second)) {
                    throw new InvalidDataException("Invalid second of minute: " + second);
                }
                if (!ChronoField.NANO_OF_SECOND.range().isValidValue(nano)) {
                    throw new InvalidDataException("Invalid nanosecond of second: " + nano);
                }
                return LocalDateTime.of(year, month, day, hour, minute, second, nano);
            }

            @Override
            public DataContainer translate(LocalDateTime obj) throws InvalidDataException {
                return DataContainer.createNew()
                        .set(DataQueries.LOCAL_DATE_YEAR, obj.getYear())
                        .set(DataQueries.LOCAL_DATE_MONTH, obj.getMonth())
                        .set(DataQueries.LOCAL_DATE_DAY, obj.getDayOfMonth())
                        .set(DataQueries.LOCAL_TIME_HOUR, obj.getHour())
                        .set(DataQueries.LOCAL_TIME_MINUTE, obj.getMinute())
                        .set(DataQueries.LOCAL_TIME_SECOND, obj.getSecond())
                        .set(DataQueries.LOCAL_TIME_NANO, obj.getNano());
            }
        };
        ZONED_DATE_TIME_DATA_SERIALIZER = new AbstractDataTranslator<ZonedDateTime>(
                "sponge", "zoned_date_time", "ZonedDateTimeTranslator", TypeToken.of(ZonedDateTime.class)) {

            @Override
            public ZonedDateTime translate(DataView view) throws InvalidDataException {
                final int year = view.getInt(DataQueries.LOCAL_DATE_YEAR).orElseThrow(invalidDataQuery(DataQueries.LOCAL_DATE_YEAR));
                final int month = view.getInt(DataQueries.LOCAL_DATE_MONTH).orElseThrow(invalidDataQuery(DataQueries.LOCAL_DATE_MONTH));
                final int day = view.getInt(DataQueries.LOCAL_DATE_DAY).orElseThrow(invalidDataQuery(DataQueries.LOCAL_DATE_DAY));
                final int hour = view.getInt(DataQueries.LOCAL_TIME_HOUR).orElseThrow(invalidDataQuery(DataQueries.LOCAL_TIME_HOUR));
                final int minute = view.getInt(DataQueries.LOCAL_TIME_MINUTE).orElseThrow(invalidDataQuery(DataQueries.LOCAL_TIME_MINUTE));
                final int second = view.getInt(DataQueries.LOCAL_TIME_SECOND).orElseThrow(invalidDataQuery(DataQueries.LOCAL_TIME_SECOND));
                final int nano = view.getInt(DataQueries.LOCAL_TIME_NANO).orElseThrow(invalidDataQuery(DataQueries.LOCAL_TIME_NANO));
                final String zoneId = view.getString(DataQueries.ZONE_TIME_ID).orElseThrow(invalidDataQuery(DataQueries.ZONE_TIME_ID));
                if (!ChronoField.YEAR.range().isValidValue(year)) {
                    throw new InvalidDataException("Invalid year: " + year);
                }
                if (!ChronoField.MONTH_OF_YEAR.range().isValidValue(month)) {
                    throw new InvalidDataException("Invalid month of year: " + month);
                }
                if (!ChronoField.DAY_OF_MONTH.range().isValidValue(day)) {
                    throw new InvalidDataException("Invalid day of month: " + day);
                }
                if (!ChronoField.HOUR_OF_DAY.range().isValidValue(hour)) {
                    throw new InvalidDataException("Invalid hour of day: " + hour);
                }
                if (!ChronoField.MINUTE_OF_HOUR.range().isValidValue(minute)) {
                    throw new InvalidDataException("Invalid minute of hour: " + minute);
                }
                if (!ChronoField.SECOND_OF_MINUTE.range().isValidValue(second)) {
                    throw new InvalidDataException("Invalid second of minute: " + second);
                }
                if (!ChronoField.NANO_OF_SECOND.range().isValidValue(nano)) {
                    throw new InvalidDataException("Invalid nanosecond of second: " + nano);
                }
                if (!ZoneId.getAvailableZoneIds().contains(zoneId)) {
                    throw new InvalidDataException("Unrecognized ZoneId: " + zoneId);
                }
                return ZonedDateTime.of(LocalDate.of(year, month, day), LocalTime.of(hour, minute, second, nano), ZoneId.of(zoneId));
            }

            @Override
            public DataContainer translate(ZonedDateTime obj) throws InvalidDataException {
                return DataContainer.createNew()
                        .set(DataQueries.LOCAL_DATE_YEAR, obj.getYear())
                        .set(DataQueries.LOCAL_DATE_MONTH, obj.getMonth())
                        .set(DataQueries.LOCAL_DATE_DAY, obj.getDayOfMonth())
                        .set(DataQueries.LOCAL_TIME_HOUR, obj.getHour())
                        .set(DataQueries.LOCAL_TIME_MINUTE, obj.getMinute())
                        .set(DataQueries.LOCAL_TIME_SECOND, obj.getSecond())
                        .set(DataQueries.LOCAL_TIME_NANO, obj.getNano())
                        .set(DataQueries.ZONE_TIME_ID, obj.getZone().getId());
            }
        };
        INSTANT_DATA_SERIALIZER = new AbstractDataTranslator<Instant>(
                "sponge", "instant", "InstantTranslator", TypeToken.of(Instant.class)) {

            @Override
            public Instant translate(DataView view) throws InvalidDataException {
                final int year = view.getInt(DataQueries.LOCAL_DATE_YEAR).orElseThrow(invalidDataQuery(DataQueries.LOCAL_DATE_YEAR));
                final int month = view.getInt(DataQueries.LOCAL_DATE_MONTH).orElseThrow(invalidDataQuery(DataQueries.LOCAL_DATE_MONTH));
                final int day = view.getInt(DataQueries.LOCAL_DATE_DAY).orElseThrow(invalidDataQuery(DataQueries.LOCAL_DATE_DAY));
                final int hour = view.getInt(DataQueries.LOCAL_TIME_HOUR).orElseThrow(invalidDataQuery(DataQueries.LOCAL_TIME_HOUR));
                final int minute = view.getInt(DataQueries.LOCAL_TIME_MINUTE).orElseThrow(invalidDataQuery(DataQueries.LOCAL_TIME_MINUTE));
                final int second = view.getInt(DataQueries.LOCAL_TIME_SECOND).orElseThrow(invalidDataQuery(DataQueries.LOCAL_TIME_SECOND));
                final int nano = view.getInt(DataQueries.LOCAL_TIME_NANO).orElseThrow(invalidDataQuery(DataQueries.LOCAL_TIME_NANO));
                if (!ChronoField.YEAR.range().isValidValue(year)) {
                    throw new InvalidDataException("Invalid year: " + year);
                }
                if (!ChronoField.MONTH_OF_YEAR.range().isValidValue(month)) {
                    throw new InvalidDataException("Invalid month of year: " + month);
                }
                if (!ChronoField.DAY_OF_MONTH.range().isValidValue(day)) {
                    throw new InvalidDataException("Invalid day of month: " + day);
                }
                if (!ChronoField.HOUR_OF_DAY.range().isValidValue(hour)) {
                    throw new InvalidDataException("Invalid hour of day: " + hour);
                }
                if (!ChronoField.MINUTE_OF_HOUR.range().isValidValue(minute)) {
                    throw new InvalidDataException("Invalid minute of hour: " + minute);
                }
                if (!ChronoField.SECOND_OF_MINUTE.range().isValidValue(second)) {
                    throw new InvalidDataException("Invalid second of minute: " + second);
                }
                if (!ChronoField.NANO_OF_SECOND.range().isValidValue(nano)) {
                    throw new InvalidDataException("Invalid nanosecond of second: " + nano);
                }
                return LocalDateTime.of(year, month, day, hour, minute, second, nano).toInstant(ZoneOffset.UTC);
            }

            @Override
            public DataContainer translate(Instant obj) throws InvalidDataException {
                final LocalDateTime local = obj.atZone(ZoneOffset.UTC).toLocalDateTime();
                return DataContainer.createNew()
                        .set(DataQueries.LOCAL_DATE_YEAR, local.getYear())
                        .set(DataQueries.LOCAL_DATE_MONTH, local.getMonth())
                        .set(DataQueries.LOCAL_DATE_DAY, local.getDayOfMonth())
                        .set(DataQueries.LOCAL_TIME_HOUR, local.getHour())
                        .set(DataQueries.LOCAL_TIME_MINUTE, local.getMinute())
                        .set(DataQueries.LOCAL_TIME_SECOND, local.getSecond())
                        .set(DataQueries.LOCAL_TIME_NANO, local.getNano());
            }
        };
        MONTH_DATA_SERIALIZER = new DataTranslator<Month>() {

            final TypeToken<Month> token = TypeToken.of(Month.class);

            @Override
            public TypeToken<Month> getToken() {
                return this.token;
            }

            @Override
            public Month translate(DataView view) throws InvalidDataException {
                final int month = view.getInt(DataQueries.LOCAL_DATE_MONTH).orElseThrow(invalidDataQuery(DataQueries.LOCAL_DATE_MONTH));
                if (!ChronoField.MONTH_OF_YEAR.range().isValidValue(month)) {
                    throw new InvalidDataException("Invalid month of year: " + month);
                }
                return Month.of(month);
            }

            @Override
            public DataContainer translate(Month obj) throws InvalidDataException {
                return DataContainer.createNew().set(DataQueries.LOCAL_DATE_MONTH, obj.getValue());
            }

            @Override
            public DataView addTo(Month obj, DataView dataView) {
                return dataView.set(DataQueries.LOCAL_DATE_MONTH, obj.getValue());
            }

            @Override
            public String getId() {
                return "sponge:month";
            }

            @Override
            public String getName() {
                return "JavaMonthTranslator";
            }
        };
    }

    private static Supplier<InvalidDataException> invalidDataQuery(DataQuery query) {
        return () -> new InvalidDataException("Invalid data located at: " + query.toString());
    }

    public static void registerSerializers(DataManager dataManager) {
        dataManager.registerTranslator(UUID.class, UUID_DATA_SERIALIZER);
        dataManager.registerTranslator(Vector2d.class, VECTOR_2_D_DATA_SERIALIZER);
        dataManager.registerTranslator(Vector2f.class, VECTOR_2_F_DATA_SERIALIZER);
        dataManager.registerTranslator(Vector2i.class, VECTOR_2_I_DATA_SERIALIZER);
        dataManager.registerTranslator(Vector2l.class, VECTOR_2_L_DATA_SERIALIZER);
        dataManager.registerTranslator(Vector3d.class, VECTOR_3_D_DATA_SERIALIZER);
        dataManager.registerTranslator(Vector3f.class, VECTOR_3_F_DATA_SERIALIZER);
        dataManager.registerTranslator(Vector3i.class, VECTOR_3_I_DATA_SERIALIZER);
        dataManager.registerTranslator(Vector3l.class, VECTOR_3_L_DATA_SERIALIZER);
        dataManager.registerTranslator(Vector4d.class, VECTOR_4_D_DATA_SERIALIZER);
        dataManager.registerTranslator(Vector4f.class, VECTOR_4_F_DATA_SERIALIZER);
        dataManager.registerTranslator(Vector4i.class, VECTOR_4_I_DATA_SERIALIZER);
        dataManager.registerTranslator(Vector4l.class, VECTOR_4_L_DATA_SERIALIZER);
        dataManager.registerTranslator(Complexd.class, COMPLEXD_DATA_SERIALIZER);
        dataManager.registerTranslator(Complexf.class, COMPLEXF_DATA_SERIALIZER);
        dataManager.registerTranslator(Quaterniond.class, QUATERNIOND_DATA_SERIALIZER);
        dataManager.registerTranslator(Quaternionf.class, QUATERNIONF_DATA_SERIALIZER);
        dataManager.registerTranslator(LocalTime.class, LOCAL_TIME_DATA_SERIALIZER);
        dataManager.registerTranslator(LocalDate.class, LOCAL_DATE_DATA_SERIALIZER);
        dataManager.registerTranslator(LocalDateTime.class, LOCAL_DATE_TIME_DATA_SERIALIZER);
        dataManager.registerTranslator(ZonedDateTime.class, ZONED_DATE_TIME_DATA_SERIALIZER);
        dataManager.registerTranslator(Instant.class, INSTANT_DATA_SERIALIZER);
        dataManager.registerTranslator(Month.class, MONTH_DATA_SERIALIZER);
    }

}
