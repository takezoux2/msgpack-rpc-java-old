package org.msgpack.rpc;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;

import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;
import org.msgpack.MessagePack;
import org.msgpack.packer.Packer;
import org.msgpack.type.Value;
import org.msgpack.annotation.MessagePackMessage;

import static org.hamcrest.CoreMatchers.*;


public class MethodTest  {

	@MessagePackMessage
	public static class ErrorSample{
		public ErrorSample(){}
		public ErrorSample(int code){this.code = code;}
		
		public int code;
		
		@Override
		public boolean equals(Object obj) {
			return obj instanceof ErrorSample && ((ErrorSample)obj).code == this.code;
		}
		@Override
		public String toString() {
			return "ErrorSample:" + code;
		}
	}
	@MessagePackMessage
	public static class ResultSample{
		public ResultSample(){}
		public ResultSample(String msg){resultMessage = msg;}
		public String resultMessage = "";

		@Override
		public boolean equals(Object obj) {
			return obj instanceof ResultSample && ((ResultSample)obj).resultMessage.endsWith(this.resultMessage);
		}
	}

    static MessagePack messagePack;
    @BeforeClass
    public static void beforeClass(){
         messagePack = new MessagePack();
    }

	@Test
	public void testUnpackMethod() throws IOException{
		
		messagePack.register(ErrorSample.class);
		messagePack.register(ResultSample.class);
		
		int messageId = 1;
	    /*Object[] call = new Object[]{messageId , methodName , null , null};
	    MessagePack.pack(call);*/
		

		{
			ByteArrayOutputStream out = new ByteArrayOutputStream();
			Packer packer = messagePack.createPacker(out);// new Packer(out);
			packer.writeArrayBegin(4);
			packer.write(1);
			packer.write(messageId);
			packer.write(new ErrorSample(5));
			packer.writeNil();
            packer.writeArrayEnd();
			
			
			Value obj = messagePack.read(out.toByteArray());
			
			Value[] array = obj.asArrayValue().getElementArray();
			Assert.assertThat(array.length, is(4));
			Assert.assertThat(array[0].asIntegerValue().getInt(), is(1));
			Assert.assertThat(array[1].asIntegerValue().getInt(), is(messageId));
			Assert.assertThat(messagePack.convert( array[2],ErrorSample.class),is(new ErrorSample(5)));
			Assert.assertThat(messagePack.convert( array[3],ResultSample.class), nullValue());
		}
		{
			ByteArrayOutputStream out = new ByteArrayOutputStream();
			Packer packer = messagePack.createPacker(out);
			packer.writeArrayBegin(4);
			packer.write(1);
			packer.write(messageId);
			packer.writeNil();
			packer.write(new ResultSample("fuga"));
            packer.writeArrayEnd();
			
			
			Value obj = MessagePack.unpack(out.toByteArray());
			
			Value[] array = obj.asArrayValue().getElementArray();
			Assert.assertThat(array.length, is(4));
			Assert.assertThat(array[0].asIntegerValue().getInt(), is(1));
			Assert.assertThat(array[1].asIntegerValue().getInt(), is(messageId));
			Assert.assertThat(messagePack.convert( array[2],ErrorSample.class),nullValue());
			Assert.assertThat(messagePack.convert( array[3],ResultSample.class),is(new ResultSample("fuga")));
		}
		
		{
			ByteArrayOutputStream out = new ByteArrayOutputStream();
			Packer packer = messagePack.createPacker(out);
			packer.writeArrayBegin(4);
			packer.write(1);
			packer.write(messageId);
			packer.writeNil();
			packer.writeNil();
            packer.writeArrayEnd();
			
			
			Value obj = MessagePack.unpack(out.toByteArray());
			
			Value[] array = obj.asArrayValue().getElementArray();
			Assert.assertThat(array.length, is(4));
			Assert.assertThat(array[0].asIntegerValue().getInt(), is(1));
			Assert.assertThat(array[1].asIntegerValue().getInt(), is(messageId));
			Assert.assertThat(messagePack.convert( array[2],ErrorSample.class),nullValue());
			Assert.assertThat(messagePack.convert( array[3],ResultSample.class), nullValue());
		}
		
		
	}
}
