#include "stm32f4xx_conf.h"
#include "stm32f4xx_gpio.h"
#include "stm32f4xx_rcc.h"
#include "tm_stm32f4_pcd8544.h"


/*
	obs³uga wywietlacza: biblioteka  Tilena Majerle
	display handle powered by Tilen Majerle's library
*/

int main(void) {

	SystemInit();


	PCD8544_Init(0x38);





    while (1) {


  //  	TODO : czytanie z pliku		//

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




    	PCD8544_Refresh();

    }
}
