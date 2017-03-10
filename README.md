# String2Bitmap
将文字生成图片的Utils
      bitmapToByteArrayForPrinter方法
      该方法比较容易理解：
      1.方法参数是待打印的文字的List集合，其中存放的就是一行行待打印的文字
      2.读入参数后，先配置生成图片的宽度[为8的倍数]，然后把每一行文字读入计，并计算这一行是否可以完全显示，如果不能一行显示需要多少行
      3.设置文字的大小高度并计算一行文字的高度
      4.生成空的位图
      5.开始遍历List集合，绘制每一行
      6.返回生成的图片
      对于图片的合并，这个比文字生成图片更简单一点：
      1.取出两张位图，把最宽的设置为宽度，高度之和设置为生成图片的高度
      int width = Math.max(first.getWidth(), second.getWidth());
      int height = first.getHeight() + second.getHeight();
      2.这里比较麻烦的就是 logo的位置必须居中显示，所以我们需要把logo绘制的起点改变一下
      int startWidth = (width - first.getWidth()) / 2;
      3.绘制即可
      Bitmap result = Bitmap.createBitmap(width, height, Bitmap.Config.RGB_565);
      Canvas canvas = new Canvas(result);
      canvas.drawBitmap(first, startWidth, 0, null);
      canvas.drawBitmap(second, 0, first.getHeight(), null);
      原本List集合是文字，在List集合泛型的时候，不能在单纯的传入一个字段了，而是携带信息的参数
				   /**
				     * @param text           等待打印的字段
				     * @param isRightOrLeft  可空，默认右边
				     * @param isSmallOrLarge 可空，默认小字
				     */
				    public StringBitmapParameter(String text, int isRightOrLeft, int isSmallOrLarge) {
				        this.text = text;
				        this.isRightOrLeft = isRightOrLeft;
				        this.isSmallOrLarge = isSmallOrLarge;
				    }
			 在把文字构建图片的时候，由于是List存储的既是一行，所以我们这样做可以把每一行设置成左便开始打印还是右边，文字设置为大还是小，但是不能把文字的字体样式修改了
			 	 for (StringBitmapParameter mParameter : mBreakString) {
			            String str = mParameter.getText();

			            if (mParameter.getIsSmallOrLarge() == IS_SMALL) {
			                paint.setTextSize(SMALL_TEXT);
			            } else if (mParameter.getIsSmallOrLarge() == IS_LARGE) {
			                paint.setTextSize(LARGE_TEXT);
			            }

			            if (mParameter.getIsRightOrLeft() == IS_RIGHT) {
			                x = WIDTH - paint.measureText(str);
			            } else if (mParameter.getIsRightOrLeft() == IS_LEFT) {
			                x = START_LEFT;
			            } else if (mParameter.getIsRightOrLeft() == IS_CENTER) {
			                x = (WIDTH - paint.measureText(str)) / 2.0f;
			            }

			            if (str.isEmpty() | str.contains("\n") | mParameter.getIsSmallOrLarge() == IS_LARGE) {
			                canvas.drawText(str, x, y + FontHeight / 2, paint);
			                y = y + FontHeight;
			            } else {
			                canvas.drawText(str, x, y, paint);
			            }
			            y = y + FontHeight;
			        }
			 这里展示的即是关键位置
			 在图片的压缩这一部分，之前是ARGB_8888的，现在修改为了RGB_565，基本占内存小了一半，前者一个像素32位，后则16位，还有个模式是ALPHA_8仅仅有8位，但是这个模式下，打印出来的是纯黑的玩意，他只有透明度，没有颜色，所以不行，打印的时候也是把它变成黑白的打印的，而且这应该是压缩的极致了
			 现在就是合并图片生成的太慢，我觉得是我这里导致的：
			   for (int i = 0; i < result.getWidth(); i++) {
		            for (int j = 0; j < result.getHeight(); j++) {
		                result.setPixel(i, j, Color.WHITE);
		            }
		        }
		     这个循环是为了把没个像素点都变成白的，然后再绘图，因为不这样做的话，图片打印出来，周边会有黑色条文，就是因为没有配色，他是透明的，所以打出来就是黑的导致的
		     所以如果把这个O(n^2)改变的话，就应该能加快生成图片的速度
