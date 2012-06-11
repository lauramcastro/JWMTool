package jwmtool.util;

/**
 * This class is used to perform the forward and inverse discrete cosine transform (DCT)
 * as specified by the CCITT TI.81 recommendation (www.w3.org/Graphics/JPEG/itu-t81.pdf).
 * The implementation of the IDCT and FDCT algorithms are based on the jfdctflt.c and
 * jidctflt.c implementations written by Thomas G. Lane.
 */
 
public class FloatDCT {
 
	private static final double R2 = Math.sqrt(2);
	private static final float _R2 = (float) R2;
 
	//--------------------------------------------------------------------------------------
	// these values are used in the IDCT
 
	private static final double[] scaleFactor = {	1.0,					// 1.0
							Math.cos(1 * Math.PI / 16) * R2,	// 1.3870398453221475
							Math.cos(2 * Math.PI / 16) * R2,	// 1.3065629648763766
							Math.cos(3 * Math.PI / 16) * R2,	// 1.1758756024193588
							Math.cos(4 * Math.PI / 16) * R2,	// 1.0
							Math.cos(5 * Math.PI / 16) * R2,	// 0.7856949583871023
							Math.cos(6 * Math.PI / 16) * R2,	// 0.5411961001461971
							Math.cos(7 * Math.PI / 16) * R2 };	// 0.2758993792829431
 
	private static final float[] _scaleFactor = {	(float) scaleFactor[0],
							(float) scaleFactor[1],
							(float) scaleFactor[2],
							(float) scaleFactor[3],
							(float) scaleFactor[4],
							(float) scaleFactor[5],
							(float) scaleFactor[6],
							(float) scaleFactor[7] };
 
	private static final double K2 = 2 * Math.cos(Math.PI / 8);	// 1.8477590650225735
	private static final double K6 = 2 * Math.sin(Math.PI / 8);	// 0.7653668647301796
	private static final double M26 = K2 - K6;			// 1.0823922002923938
	private static final double P26 = -(K2 + K6);			// -2.613125929752753
 
	private static final float _K2 = (float) K2;
	private static final float _K6 = (float) K6;
	private static final float _M26 = (float) M26;
	private static final float _P26 = (float) P26;
 
	//--------------------------------------------------------------------------------------
 
 
 
	//--------------------------------------------------------------------------------------
	// these values are used in the FDCT
 
	private static final double F0 = 1.0 / R2;				// 0.7071067811865475
	private static final double F1 = Math.cos(1 * Math.PI / 16) / 2;	// 0.4903926402016152
	private static final double F2 = Math.cos(2 * Math.PI / 16) / 2;	// 0.46193976625564337
	private static final double F3 = Math.cos(3 * Math.PI / 16) / 2;	// 0.4157348061512726
	private static final double F4 = Math.cos(4 * Math.PI / 16) / 2;	// 0.3535533905932738
	private static final double F5 = Math.cos(5 * Math.PI / 16) / 2;	// 0.27778511650980114
	private static final double F6 = Math.cos(6 * Math.PI / 16) / 2;	// 0.19134171618254492
	private static final double F7 = Math.cos(7 * Math.PI / 16) / 2;	// 0.09754516100806417
	private static final double D71 = F7 - F1;				// -0.39284747919355106
	private static final double D35 = F3 - F5;				// 0.13794968964147147
	private static final double D62 = F6 - F2;				// -0.27059805007309845
	private static final double S71 = F7 + F1;				// 0.5879378012096794
	private static final double S35 = F3 + F5;				// 0.6935199226610738
	private static final double S62 = F6 + F2;				// 0.6532814824381883
 
	private static final float _F0 = (float) F0;
	private static final float _F1 = (float) F1;
	private static final float _F2 = (float) F2;
	private static final float _F3 = (float) F3;
	private static final float _F4 = (float) F4;
	private static final float _F5 = (float) F5;
	private static final float _F6 = (float) F6;
	private static final float _F7 = (float) F7;
	private static final float _D71 = (float) D71;
	private static final float _D35 = (float) D35;
	private static final float _D62 = (float) D62;
	private static final float _S71 = (float) S71;
	private static final float _S35 = (float) S35;
	private static final float _S62 = (float) S62;
 
	//--------------------------------------------------------------------------------------
 
	/**
	This method performs the forward discrete cosine transform (FDCT).
	Both the in and out arrays are 8x8.
	*/
	public static void FDCT(float[][] in, float[][] out) {
		float temp;
		float a0, a1, a2, a3, a4, a5, a6, a7;
		float b0, b1, b2, b3, b4, b5, b6, b7;
 
		// Horizontal transform
		for(int i = 0; i < 8; i++) {
			b0 = in[i][0] + in[i][7];
			b7 = in[i][0] - in[i][7];
			b1 = in[i][1] + in[i][6];
			b6 = in[i][1] - in[i][6];
			b2 = in[i][2] + in[i][5];
			b5 = in[i][2] - in[i][5];
			b3 = in[i][3] + in[i][4];
			b4 = in[i][3] - in[i][4];
 
			a0 = b0 + b3;
			a1 = b1 + b2;
			a2 = b1 - b2;
			a3 = b0 - b3;
			a4 = b4;
			a5 = (b6 - b5) * _F0;
			a6 = (b6 + b5) * _F0;
			a7 = b7;
			out[i][0] = (a0 + a1) * _F4;
			out[i][4] = (a0 - a1) * _F4;
 
			temp = (a3 + a2) * _F6;
			out[i][2] = temp - a3 * _D62;
			out[i][6] = temp - a2 * _S62;
 
			b4 = a4 + a5;
			b7 = a7 + a6;
			b5 = a4 - a5;
			b6 = a7 - a6;
 
			temp = (b7 + b4) * _F7;
			out[i][1] = temp - b7 * _D71;
			out[i][7] = temp - b4 * _S71;
 
			temp = (b6 + b5) * _F3;
			out[i][5] = temp - b6 * _D35;
			out[i][3] = temp - b5 * _S35;
		}
 
 
		// Vertical transform
		for(int i = 0; i < 8; i++) {
			b0 = out[0][i] + out[7][i];
			b7 = out[0][i] - out[7][i];
			b1 = out[1][i] + out[6][i];
			b6 = out[1][i] - out[6][i];
			b2 = out[2][i] + out[5][i];
			b5 = out[2][i] - out[5][i];
			b3 = out[3][i] + out[4][i];
			b4 = out[3][i] - out[4][i];
 
			a0 = b0 + b3;
			a1 = b1 + b2;
			a2 = b1 - b2;
			a3 = b0 - b3;
			a4 = b4;
			a5 = (b6 - b5) * _F0;
			a6 = (b6 + b5) * _F0;
			a7 = b7;
			out[0][i] = (a0 + a1) * _F4;
			out[4][i] = (a0 - a1) * _F4;
 
			temp = (a3 + a2) * _F6;
			out[2][i] = temp - a3 * _D62;
			out[6][i] = temp - a2 * _S62;
 
			b4 = a4 + a5;
			b7 = a7 + a6;
			b5 = a4 - a5;
			b6 = a7 - a6;
 
			temp = (b7 + b4) * _F7;
			out[1][i] = temp - b7 * _D71;
			out[7][i] = temp - b4 * _S71;
 
			temp = (b6 + b5) * _F3;
			out[5][i] = temp - b6 * _D35;
			out[3][i] = temp - b5 * _S35;
		}
	}
 
	// this method is actually a little faster than the [][] version
	public static void IDCT(float[] in, float[] quant, float[] out) {
		float tmp0, tmp1, tmp2, tmp3, tmp4, tmp5, tmp6;
		float tmp7, tmp8, tmp9, tmp10, tmp11, tmp12, tmp13;
		float z5, z10, z11, z12, z13;
 
		for (int i = 0; i < 8; i++) {
			if (in[8+i] == 0 && in[16+i] == 0 && in[24+i] == 0 && in[32+i] == 0 && in[40+i] == 0 && in[48+i] == 0 && in[56+i] == 0) {
				float dc = in[i] * quant[i];
				out[i] = dc;
				out[8+i] = dc;
				out[16+i] = dc;
				out[24+i] = dc;
				out[32+i] = dc;
				out[40+i] = dc;
				out[48+i] = dc;
				out[56+i] = dc;
				continue;
			}
 
			tmp0 = in[i] * quant[i];
			tmp1 = in[16+i] * quant[16+i];
			tmp2 = in[32+i] * quant[32+i];
			tmp3 = in[48+i] * quant[48+i];
 
			tmp10 = tmp0 + tmp2;
			tmp11 = tmp0 - tmp2;
 
			tmp13 = tmp1 + tmp3;
			tmp12 = (tmp1 - tmp3) * _R2 - tmp13;
 
			tmp0 = tmp10 + tmp13;
			tmp3 = tmp10 - tmp13;
			tmp1 = tmp11 + tmp12;
			tmp2 = tmp11 - tmp12;
 
			tmp4 = in[8+i] * quant[8+i];
			tmp5 = in[24+i] * quant[24+i];
			tmp6 = in[40+i] * quant[40+i];
			tmp7 = in[56+i] * quant[56+i];
 
			z13 = tmp6 + tmp5;
			z10 = tmp6 - tmp5;
			z11 = tmp4 + tmp7;
			z12 = tmp4 - tmp7;
 
			tmp7 = z11 + z13;
			tmp11 = (z11 - z13) * _R2;
 
			z5 = (z10 + z12) * _K2;
			tmp10 = _M26 * z12 - z5;
			tmp12 = _P26 * z10 + z5;
 
			tmp6 = tmp12 - tmp7;
			tmp5 = tmp11 - tmp6;
			tmp4 = tmp10 + tmp5;
 
			out[i] = tmp0 + tmp7;
			out[56+i] = tmp0 - tmp7;
			out[8+i] = tmp1 + tmp6;
			out[48+i] = tmp1 - tmp6;
			out[16+i] = tmp2 + tmp5;
			out[40+i] = tmp2 - tmp5;
			out[32+i] = tmp3 + tmp4;
			out[24+i] = tmp3 - tmp4;
		}
 
		int row = 0;
		for (int i = 0; i < 8; i++) {
			row = i * 8;
 
			tmp10 = out[row] + out[row+4];
			tmp11 = out[row] - out[row+4];
 
			tmp13 = out[row+2] + out[row+6];
			tmp12 = (out[row+2] - out[row+6]) * _R2 - tmp13;
 
			tmp0 = tmp10 + tmp13;
			tmp3 = tmp10 - tmp13;
			tmp1 = tmp11 + tmp12;
			tmp2 = tmp11 - tmp12;
 
			z13 = out[row+5] + out[row+3];
			z10 = out[row+5] - out[row+3];
			z11 = out[row+1] + out[row+7];
			z12 = out[row+1] - out[row+7];
 
			tmp7 = z11 + z13;
			tmp11 = (z11 - z13) * _R2;
 
			z5 = (z10 + z12) * _K2;
			tmp10 = _M26 * z12 - z5;
			tmp12 = _P26 * z10 + z5;
 
			tmp6 = tmp12 - tmp7;
			tmp5 = tmp11 - tmp6;
			tmp4 = tmp10 + tmp5;
 
			out[row] = tmp0 + tmp7;
			out[row+7] = tmp0 - tmp7;
			out[row+1] = tmp1 + tmp6;
			out[row+6] = tmp1 - tmp6;
			out[row+2] = tmp2 + tmp5;
			out[row+5] = tmp2 - tmp5;
			out[row+4] = tmp3 + tmp4;
			out[row+3] = tmp3 - tmp4;
		}
	}
 
	public static void IDCT(float[][] in, float[][] quant, float[][] out) {
		float tmp0, tmp1, tmp2, tmp3, tmp4, tmp5, tmp6;
		float tmp7, tmp8, tmp9, tmp10, tmp11, tmp12, tmp13;
		float z5, z10, z11, z12, z13;
 
		for (int i = 0; i < 8; i++) {
			if (in[1][i] == 0 && in[2][i] == 0 && in[3][i] == 0 && in[4][i] == 0 && in[5][i] == 0 && in[6][i] == 0 && in[7][i] == 0) {
				float dc = in[0][i] * quant[0][i];
				out[0][i] = dc;
				out[1][i] = dc;
				out[2][i] = dc;
				out[3][i] = dc;
				out[4][i] = dc;
				out[5][i] = dc;
				out[6][i] = dc;
				out[7][i] = dc;
				continue;
			}
 
			tmp0 = in[0][i] * quant[0][i];
			tmp1 = in[2][i] * quant[2][i];
			tmp2 = in[4][i] * quant[4][i];
			tmp3 = in[6][i] * quant[6][i];
 
			tmp10 = tmp0 + tmp2;
			tmp11 = tmp0 - tmp2;
 
			tmp13 = tmp1 + tmp3;
			tmp12 = (tmp1 - tmp3) * _R2 - tmp13;
 
			tmp0 = tmp10 + tmp13;
			tmp3 = tmp10 - tmp13;
			tmp1 = tmp11 + tmp12;
			tmp2 = tmp11 - tmp12;
 
			tmp4 = in[1][i] * quant[1][i];
			tmp5 = in[3][i] * quant[3][i];
			tmp6 = in[5][i] * quant[5][i];
			tmp7 = in[7][i] * quant[7][i];
 
			z13 = tmp6 + tmp5;
			z10 = tmp6 - tmp5;
			z11 = tmp4 + tmp7;
			z12 = tmp4 - tmp7;
 
			tmp7 = z11+ z13;
			tmp11 = (z11 - z13) * _R2;
 
			z5 = (z10 + z12) * _K2;
			tmp10 = _M26 * z12 - z5;
			tmp12 = _P26 * z10 + z5;
 
			tmp6 = tmp12 - tmp7;
			tmp5 = tmp11 - tmp6;
			tmp4 = tmp10 + tmp5;
 
			out[0][i] = tmp0 + tmp7;
			out[7][i] = tmp0 - tmp7;
			out[1][i] = tmp1 + tmp6;
			out[6][i] = tmp1 - tmp6;
			out[2][i] = tmp2 + tmp5;
			out[5][i] = tmp2 - tmp5;
			out[4][i] = tmp3 + tmp4;
			out[3][i] = tmp3 - tmp4;
		}
 
		for (int i = 0; i < 8; i++) {
			tmp10 = out[i][0] + out[i][4];
			tmp11 = out[i][0] - out[i][4];
 
			tmp13 = out[i][2] + out[i][6];
			tmp12 = (out[i][2] - out[i][6]) * _R2 - tmp13;
 
			tmp0 = tmp10 + tmp13;
			tmp3 = tmp10 - tmp13;
			tmp1 = tmp11 + tmp12;
			tmp2 = tmp11 - tmp12;
 
			z13 = out[i][5] + out[i][3];
			z10 = out[i][5] - out[i][3];
			z11 = out[i][1] + out[i][7];
			z12 = out[i][1] - out[i][7];
 
			tmp7 = z11 + z13;
			tmp11 = (z11 - z13) * _R2;
 
			z5 = (z10 + z12) * _K2;
			tmp10 = _M26 * z12 - z5;
			tmp12 = _P26 * z10 + z5;
 
			tmp6 = tmp12 - tmp7;
			tmp5 = tmp11 - tmp6;
			tmp4 = tmp10 + tmp5;
 
			out[i][0] = tmp0 + tmp7;
			out[i][7] = tmp0 - tmp7;
			out[i][1] = tmp1 + tmp6;
			out[i][6] = tmp1 - tmp6;
			out[i][2] = tmp2 + tmp5;
			out[i][5] = tmp2 - tmp5;
			out[i][4] = tmp3 + tmp4;
			out[i][3] = tmp3 - tmp4;
		}
	}
 
 
/*	public static void main(String[] args) {
		float[][] in = new float[8][8];
		int k = 0;
		for (int i = 0; i < 8; i++)
			for (int j = 0; j < 8; j++)
				in[i][j] = ++k;
 
		float[][] out = new float[8][8];
		FDCT(in, out);
 
		float[][] quant = new float[8][8];
		for (int i = 0; i < 8; i++)
			for (int j = 0; j < 8; j++)
				quant[i][j] = 1f;
 
		scaleQuantizationTable(quant); // important step
 
		IDCT(out, quant, in); // values may be slightly off due to precision errors (e.g. 9.999998 instead of 10)
	}
*/ 
	/**
	This method applies the pre-scaling that the IDCT(float[][], float[][], float[][]) method needs to work correctly.
	The table parameter should be 8x8, non-zigzag order.
	*/
	public static void scaleQuantizationTable(float[][] table) {
		for (int i = 0; i < 8; i++)
			for (int j = 0; j < 8; j++)
				table[i][j] = table[i][j] * _scaleFactor[i] * _scaleFactor[j] / 8;
	}
 
 
	/**
	This method applies the pre-scaling that the IDCT(float[], float[], float[]) method needs to work correctly.
	The table parameter should be 1x64, non-zigzag order.
	*/
	public static void scaleQuantizationTable(float[] table) {
		int k = 0;
		for (int i = 0; i < 8; i++) {
			for (int j = 0; j < 8; j++) {
				table[k] = table[k] * _scaleFactor[i] * _scaleFactor[j] / 8;
				k++;
			}
		}
	}
}

