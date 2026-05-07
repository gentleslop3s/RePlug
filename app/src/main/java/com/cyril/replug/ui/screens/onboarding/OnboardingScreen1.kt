package com.cyril.replug.ui.screens.onboarding

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.paint
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.cyril.replug.R
import com.cyril.replug.navigation.ROUTE_REGISTER
import com.cyril.replug.ui.theme.mainBlue

@Composable
fun Onboarding1Screen(navController: NavController){

    Column(
        modifier = Modifier
            .fillMaxSize()
            .paint(painterResource(R.drawable.replugbgimg), contentScale = ContentScale.FillBounds),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
    ) {
        Image(
            painter = painterResource(R.drawable.repluglogo),
            contentDescription = "product",
            modifier = Modifier.size(200.dp)
        )
        Spacer(modifier = Modifier.height(20.dp))

        Text(
            text = "Welcome to RePlug",
            fontSize = 30.sp,
            fontWeight = FontWeight.Bold,
            fontFamily = FontFamily.SansSerif,
            color = mainBlue
        )

        Text(
            text = "Your marketplace for gadgets and electronics.",
            fontSize = 15.sp,
            textAlign = TextAlign.Center,

            )

        Spacer(modifier = Modifier.height(20.dp))

        Button(
            onClick = {
                navController.navigate(ROUTE_REGISTER) },
            colors = ButtonDefaults.buttonColors(mainBlue),
            shape = RoundedCornerShape(15.dp),
            modifier = Modifier.width(280.dp)
        ) {
            Text(text = "Get Started!")
        }


    }



}

@Preview(showBackground = true)
@Composable
fun OnboardingScreenPreview(){

    Onboarding1Screen(rememberNavController())
}