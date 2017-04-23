#define HSE_VALUE ((uint32_t)8000000) /* STM32 discovery uses a 8Mhz external crystal */

#include "stm32f4xx_conf.h"
#include "stm32f4xx.h"
#include "stm32f4xx_gpio.h"
#include "stm32f4xx_rcc.h"
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



int main(void)
{
	/* Set up the system clocks */
	SystemInit();

	/* Initialize USB, IO, SysTick, and all those other things you do in the morning */
	init();

	/* Initialize display */
	PCD8544_Init(0x38);

	static int x = 4;

	while (1)
	{


/*
		PCD8544_GotoXY(0, 3);
		PCD8544_Puts("LeagueofLegends", PCD8544_Pixel_Set, PCD8544_FontSize_5x7);
		PCD8544_GotoXY(0, 12);
		PCD8544_Puts("Gracz 1, SILVER V", PCD8544_Pixel_Set, PCD8544_FontSize_3x5);
		PCD8544_GotoXY(0, 18);
		PCD8544_Puts("Gracz 2, GOLD IV", PCD8544_Pixel_Set, PCD8544_FontSize_3x5);
		PCD8544_GotoXY(0, 24);
		PCD8544_Puts("Gracz 3, MASTER", PCD8544_Pixel_Set, PCD8544_FontSize_3x5);
		PCD8544_GotoXY(0, 30);
		PCD8544_Puts("Gracz 4, UNRANKED", PCD8544_Pixel_Set, PCD8544_FontSize_3x5);
		PCD8544_GotoXY(0, 36);
		PCD8544_Puts("Gracz 5, BRONZE VII", PCD8544_Pixel_Set, PCD8544_FontSize_3x5);
		PCD8544_GotoXY(0, 42);
		PCD8544_Puts("MID OR FEED", PCD8544_Pixel_Set, PCD8544_FontSize_3x5);
*/






		/* Blink the orange LED at 1Hz */
		if (500 == ticker)
		{
			GPIOD->BSRRH = GPIO_Pin_13;
		}
		else if (1000 == ticker)
		{
			ticker = 0;
			GPIOD->BSRRL = GPIO_Pin_13;
		}


		/* If there's data on the virtual serial port:
		 *  - Echo it back
		 *  - Turn the green LED on for 10ms
		 */

		char * buffer;

/*
		if (VCP_get_char(&byte))
		{
			VCP_put_char(byte);
			PCD8544_GotoXY(x, 3);
			PCD8544_Puts(byte, PCD8544_Pixel_Set, PCD8544_FontSize_5x7);
			x++;
			GPIOD->BSRRL = GPIO_Pin_12;
			downTicker = 10;
		}
		if (0 == downTicker)
		{
			GPIOD->BSRRH = GPIO_Pin_12;
		}
*/

		if (VCP_get_string(&buffer)){
			PCD8544_GotoXY(0, x);
			PCD8544_Puts(&buffer, PCD8544_Pixel_Set, PCD8544_FontSize_5x7);
			x += 8;

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

/*
 * Call this to indicate a failure.  Blinks the STM32F4 discovery LEDs
 * in sequence.  At 168Mhz, the blinking will be very fast - about 5 Hz.
 * Keep that in mind when debugging, knowing the clock speed might help
 * with debugging.
 */
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
