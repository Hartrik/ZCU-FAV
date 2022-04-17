package cz.harag.vss.sp;

import java.util.Random;

/**
 * Provádí náhodné generování terénu.
 * Pro generování využívá Perlinův šum. <p>
 * Inspirace: http://devmag.org.za/2009/04/25/perlin-noise/
 *
 * @version 2016-03-24
 * @author Patrik Harag
 */
public class MapGenerator {

    private static final Random random = new Random();
    private static final int DEFAULT_OCTAVE = 8;

    /**
     * Náhodně vygeneruje mapu.
     *
     * @param w šířka
     * @param h výška
     * @param maxDepth maximální nadmořská výška
     * @return vygenerovaná mapa
     */
    public static Terrain generateMap(int w, int h, int maxDepth) {
        if (w <= 15 || h <= 15)
            throw new IllegalArgumentException("map is too small");

        return new TerrainImpl(generateHeightmap(w, h, maxDepth));
    }

    /**
     * Vygeneruje výškovou mapu.
     *
     * @param w šířka
     * @param h výška
     * @param maxHeight maximální výška terénu
     * @return pole celých čísel [h][w]
     */
    public static int[][] generateHeightmap(int w, int h, int maxHeight) {
        float[][] baseNoise = generateBaseNoise(w, h);
        float[][] perlinNoise = generatePerlinNoise(baseNoise, DEFAULT_OCTAVE);

        // přepočítání na pole celých čísel
        int[][] heightmap = new int[w][h];
        for (int i = 0; i < w; i++)
            for (int j = 0; j < h; j++)
                heightmap[i][j] = (int) (maxHeight * perlinNoise[i][j]);

        return heightmap;
    }

    private static float[][] generateBaseNoise(int width, int height) {
        float[][] noise = new float[width][height];

        for (int i = 0; i < width; i++)
            for (int j = 0; j < height; j++)
                noise[i][j] = (float) random.nextDouble() % 1;

        return noise;
    }

    private static float[][] generateSmoothNoise(float[][] baseNoise, int octave) {
        int width = baseNoise.length;
        int height = baseNoise[0].length;

        float[][] smoothNoise = new float[width][height];

        int samplePeriod = 1 << octave;  // 2 ^ k
        float sampleFrequency = 1.0f / samplePeriod;

        for (int i = 0; i < width; i++) {
            //calculate the horizontal sampling indices
            int sample_i0 = (i / samplePeriod) * samplePeriod;
            int sample_i1 = (sample_i0 + samplePeriod) % width;
            float horizontal_blend = (i - sample_i0) * sampleFrequency;

            for (int j = 0; j < height; j++) {
                //calculate the vertical sampling indices
                int sample_j0 = (j / samplePeriod) * samplePeriod;
                int sample_j1 = (sample_j0 + samplePeriod) % height;
                float vertical_blend = (j - sample_j0) * sampleFrequency;

                float top = interpolate(
                        baseNoise[sample_i0][sample_j0],
                        baseNoise[sample_i1][sample_j0], horizontal_blend);

                float bottom = interpolate(
                        baseNoise[sample_i0][sample_j1],
                        baseNoise[sample_i1][sample_j1], horizontal_blend);

                smoothNoise[i][j] = interpolate(top, bottom, vertical_blend);
            }
        }

        return smoothNoise;
    }

    private static float interpolate(float x0, float x1, float alpha) {
        return x0 * (1 - alpha) + alpha * x1;
    }

    private static float[][] generatePerlinNoise(float[][] baseNoise, int octaveCount) {
        int width = baseNoise.length;
        int height = baseNoise[0].length;

        float[][][] smoothNoise = new float[octaveCount][][];

        float persistance = 0.6f;

        // generování "hladkého" šumu
        for (int i = 0; i < octaveCount; i++) {
            smoothNoise[i] = generateSmoothNoise(baseNoise, i);
        }

        float[][] perlinNoise = new float[width][height];
        float amplitude = 1.0f;
        float totalAmplitude = 0.0f;

        // sloučení
        for (int octave = octaveCount - 1; octave >= 0; octave--) {
            amplitude *= persistance;
            totalAmplitude += amplitude;

            for (int i = 0; i < width; i++)
                for (int j = 0; j < height; j++)
                    perlinNoise[i][j] += smoothNoise[octave][i][j] * amplitude;
        }

        // normalizace
        for (int i = 0; i < width; i++)
            for (int j = 0; j < height; j++)
                perlinNoise[i][j] /= totalAmplitude;

        return perlinNoise;
    }

}