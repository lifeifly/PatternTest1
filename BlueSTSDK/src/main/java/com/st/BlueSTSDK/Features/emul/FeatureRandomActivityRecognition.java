/*******************************************************************************
 * COPYRIGHT(c) 2015 STMicroelectronics
 *
 * Redistribution and use in source and binary forms, with or without modification,
 * are permitted provided that the following conditions are met:
 *   1. Redistributions of source code must retain the above copyright notice,
 *      this list of conditions and the following disclaimer.
 *   2. Redistributions in binary form must reproduce the above copyright notice,
 *      this list of conditions and the following disclaimer in the documentation
 *      and/or other materials provided with the distribution.
 *   3. Neither the name of STMicroelectronics nor the names of its contributors
 *      may be used to endorse or promote products derived from this software
 *      without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE
 * FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL
 * DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR
 * SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER
 * CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY,
 * OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE
 * OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 *
 ******************************************************************************/
package com.st.BlueSTSDK.Features.emul;

import com.st.BlueSTSDK.Features.FeatureActivity;
import com.st.BlueSTSDK.Node;
import com.st.BlueSTSDK.NodeEmulator;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.Date;
import java.util.Random;

/**
 * generate random data for emulate the class {@link FeatureActivity}
 *
 * @author STMicroelectronics - Central Labs.
 * @version 1.0
 */
public class FeatureRandomActivityRecognition extends FeatureActivity implements NodeEmulator.EmulableFeature {

    private Random mRnd = new Random();

    public FeatureRandomActivityRecognition(Node parent) {
        super(parent);
    }

    @Override
    public byte[] generateFakeData() {

        byte [] ret = new byte[9];
        float delta = 70;

        short rndData = (short) ((0 + delta * mRnd.nextFloat()));
        ret[0] =  (byte)(rndData % 7);
        byte [] data = ByteBuffer.allocate(8).order(ByteOrder.LITTLE_ENDIAN).putLong(new Date().getTime()).array();
        System.arraycopy(data, 0, ret, 1, 8);
        return ret;
    }

}

