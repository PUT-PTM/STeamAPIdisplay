#define HSE_VALUE ((uint32_t)8000000) /* STM32 discovery uses a 8Mhz external crystal */

#include "stm32f4xx_conf.h"
#include "stm32f4xx.h"
#include "stm32f4xx_gpio.h"
#include "stm32f4xx_rcc.h"
#include "stm32f4xx_tim.h"
#include "stm32f4xx_exti.h"
#include "stm32f4xx_spi.h"
#include "usbd_cdc_core.h"
#include "usbd_usr.h"
#include "usbd_desc.h"
#include "usbd_cdc_vcp.h"
#include "usb_dcd_int.h"
#include "tm_stm32f4_gpio.h"
#include "tm_stm32f4_pcd8544.h"
#include "tm_stm32f4_spi.h"
#include "misc.h"








// obs³uga timera
// 		***	PCD8544_Clear();
//		*** page_display_iterator++;
//		*** if ( page_display_iterator > page_nr)  page_display_iterator = 0;





volatile uint32_t ticker, downTicker;

/*
 * The USB data must be 4 byte aligned if DMA is enabled. This macro handles
 * the alignment, if necessary (it's actually magic, but don't tell anyone).
 */
__ALIGN_BEGIN USB_OTG_CORE_HANDLE  USB_OTG_dev __ALIGN_END;


void init();
void ColorfulRingOfDeath(void);

/*
 * Define prototypes for interrupt handlers here. The conditional "extern"
 * ensures the weak declarations from startup_stm32f4xx.c are overridden.
 */
#ifdef __cplusplus
 extern "C" {
#endif

void SysTick_Handler(void);
void NMI_Handler(void);
void HardFault_Handler(void);
void MemManage_Handler(void);
void BusFault_Handler(void);
void UsageFault_Handler(void);
void SVC_Handler(void);
void DebugMon_Handler(void);
void PendSV_Handler(void);
void OTG_FS_IRQHandler(void);
void OTG_FS_WKUP_IRQHandler(void);

#ifdef __cplusplus
}
#endif


static int page_nr;
static int page_display_iterator;



 void TIM2_IRQHandler(void)
 {
          	if(TIM_GetITStatus(TIM2, TIM_IT_Update) != RESET)
          	{
          		GPIO_ToggleBits(GPIOD, GPIO_Pin_13);
          		PCD8544_Clear();
          		page_display_iterator++;
          		if ( page_display_iterator >= page_nr) page_display_iterator = 0;

                 	TIM_ClearITPendingBit(TIM2, TIM_IT_Update);
          	}
 }



int main(void)
{
	/* Set up the system clocks */
	SystemInit();

	/* Initialize USB, IO, SysTick, and all those other things you do in the morning */
	init();

	/* Initialize display */
	PCD8544_Init(0x39);
	PCD8544_Clear();

	char znak;
	char * buffer;
	page_display_iterator = 0;
	page_nr = 0;
	static int line_nr = 0;
	static int x1 = 4;
    static int x2 = 13;
    static int x3 = 22;
 	static int y = 1;




	struct page{
	 		char value1[14];
	 		char value2[14];
	 		char value3[14];
	 		 int flag;
	 		 int page_nr;
	 	} line[12];


	 int local_it;
	 for (local_it = 0; local_it < 12; local_it++){
		 line[local_it].flag = 0;
		 int clear;
		 for (clear = 0; clear < 14; clear++){
			line[local_it].value1[clear] = 0;
			line[local_it].value2[clear] = 0;
			line[local_it].value3[clear] = 0;
		 }

	 }


	 RCC_APB1PeriphClockCmd(RCC_APB1Periph_TIM2, ENABLE);
	 TIM_TimeBaseInitTypeDef TIM_TimeBaseStructure;
	 TIM_TimeBaseStructure.TIM_Period = 25199;
	 TIM_TimeBaseStructure.TIM_Prescaler = 9999;
	 TIM_TimeBaseStructure.TIM_ClockDivision = TIM_CKD_DIV1;
	 TIM_TimeBaseStructure.TIM_CounterMode = TIM_CounterMode_Up;
	 TIM_TimeBaseInit(TIM2, &TIM_TimeBaseStructure);
	 TIM_Cmd(TIM2, ENABLE);



	 NVIC_InitTypeDef NVIC_InitStructure;
	 NVIC_InitStructure.NVIC_IRQChannel = TIM2_IRQn;
	 NVIC_InitStructure.NVIC_IRQChannelPreemptionPriority = 0x00;
	 NVIC_InitStructure.NVIC_IRQChannelSubPriority = 0x00;
	 NVIC_InitStructure.NVIC_IRQChannelCmd = ENABLE;
	 NVIC_Init(&NVIC_InitStructure);

	 TIM_ClearITPendingBit(TIM2, TIM_IT_Update);
	 TIM_ITConfig(TIM2, TIM_IT_Update, ENABLE);

	 PCD8544_Clear();


	 local_it = 0;

	 while(1){




		if (VCP_get_string(&buffer)){
				switch(line_nr){
					case(0):
						{

						strcpy(line[page_nr].value1, &buffer);
						line_nr++;
						break;
						}
					case(1):{
						strcpy(line[page_nr].value2, &buffer);
						line_nr++;
						break;
					}
					case(2):{
						strcpy(line[page_nr].value3, &buffer);
						line_nr=0;
						line[page_nr].page_nr = page_nr;
						page_nr++;
						line[page_nr].flag = 1;
						break;
					}
				}




		}


		for (local_it = 0; local_it <= 12; local_it++){

					if (line[local_it].page_nr == page_display_iterator){
						if (line[local_it].flag==1){
							PCD8544_GotoXY(y, x1);
							PCD8544_Puts(line[local_it].value1, PCD8544_Pixel_Set, PCD8544_FontSize_5x7);
							PCD8544_GotoXY(y, x2);
							PCD8544_Puts(line[local_it].value2, PCD8544_Pixel_Set, PCD8544_FontSize_5x7);
							PCD8544_GotoXY(y, x3);
							PCD8544_Puts(line[local_it].value3, PCD8544_Pixel_Set, PCD8544_FontSize_5x7);
						}
					}

			}


				/* refresh display */



		PCD8544_Refresh();
	 }



	return 0;

}






void init()
{
	/* STM32F4 discovery LEDs */
	GPIO_InitTypeDef LED_Config;

	/* Always remember to turn on the peripheral clock...  If not, you may be up till 3am debugging... */
	RCC_AHB1PeriphClockCmd(RCC_AHB1Periph_GPIOD, ENABLE);
	LED_Config.GPIO_Pin = GPIO_Pin_12 | GPIO_Pin_13| GPIO_Pin_14| GPIO_Pin_15;
	LED_Config.GPIO_Mode = GPIO_Mode_OUT;
	LED_Config.GPIO_OType = GPIO_OType_PP;
	LED_Config.GPIO_Speed = GPIO_Speed_25MHz;
	LED_Config.GPIO_PuPd = GPIO_PuPd_NOPULL;
	GPIO_Init(GPIOD, &LED_Config);



	/* Setup SysTick or CROD! */
	if (SysTick_Config(SystemCoreClock / 1000))
	{
		ColorfulRingOfDeath();
	}


	/* Setup USB */
	USBD_Init(&USB_OTG_dev,
	            USB_OTG_FS_CORE_ID,
	            &USR_desc,
	            &USBD_CDC_cb,
	            &USR_cb);

	return;
}

void ColorfulRingOfDeath(void)
{
	uint16_t ring = 1;
	while (1)
	{
		uint32_t count = 0;
		while (count++ < 500000);

		GPIOD->BSRRH = (ring << 12);
		ring = ring << 1;
		if (ring >= 1<<4)
		{
			ring = 1;
		}
		GPIOD->BSRRL = (ring << 12);
	}
}

/*
 * Interrupt Handlers
 */

void SysTick_Handler(void)
{
	ticker++;
	if (downTicker > 0)
	{
		downTicker--;
	}
}

void NMI_Handler(void)       {}
void HardFault_Handler(void) { ColorfulRingOfDeath(); }
void MemManage_Handler(void) { ColorfulRingOfDeath(); }
void BusFault_Handler(void)  { ColorfulRingOfDeath(); }
void UsageFault_Handler(void){ ColorfulRingOfDeath(); }
void SVC_Handler(void)       {}
void DebugMon_Handler(void)  {}
void PendSV_Handler(void)    {}

void OTG_FS_IRQHandler(void)
{
  USBD_OTG_ISR_Handler (&USB_OTG_dev);
}

void OTG_FS_WKUP_IRQHandler(void)
{
  if(USB_OTG_dev.cfg.low_power)
  {
    *(uint32_t *)(0xE000ED10) &= 0xFFFFFFF9 ;
    SystemInit();
    USB_OTG_UngateClock(&USB_OTG_dev);
  }
  EXTI_ClearITPendingBit(EXTI_Line18);
}
