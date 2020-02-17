package com.marknjunge.ledger.data.models

import android.annotation.SuppressLint
import android.os.Parcelable
import com.google.firebase.crashlytics.FirebaseCrashlytics
import com.marknjunge.ledger.utils.DateTime
import kotlinx.android.parcel.Parcelize
import java.lang.Exception

/**
 * Created by MarkNjunge.
 * mark.kamau@outlook.com
 * https://github.com/MarkNjunge
 */
@Parcelize
data class MpesaMessage(
    val body: String,
    val code: String,
    val transactionType: TransactionType,
    val amount: Double,
    val accountNumber: String?,
    val transactionDate: Long,
    val balance: Double,
    val transactionCost: Double
) : Parcelable {

    companion object {
        @SuppressLint("DefaultLocale")
        fun create(body: String): MpesaMessage {
            try {
                val code = body.split(Regex("( [Cc]onfirmed)"))[0].reversed().split(" ")[0].reversed()

                val bodyLowerCase = body.toLowerCase()

                val transactionType = when {
                    bodyLowerCase.contains(Regex("(.*) reversal (.*)")) -> TransactionType.REVERSAL
                    bodyLowerCase.contains(Regex("(.*) sent to (.*) for account (.*)")) -> TransactionType.PAY_BILL
                    bodyLowerCase.contains(Regex("(.*) paid to")) -> TransactionType.BUY_GOODS
                    bodyLowerCase.contains(Regex("(.*) sent to (.*)")) -> TransactionType.SEND
                    bodyLowerCase.contains(Regex("(.*)withdraw (.*)")) -> TransactionType.WITHDRAW
                    bodyLowerCase.contains(Regex("(.*) received (.*)")) -> TransactionType.RECEIVE
                    bodyLowerCase.contains(Regex("(.*) airtime (.*)")) -> TransactionType.AIRTIME
                    bodyLowerCase.contains(Regex("(.*)your m-pesa balance (.*)")) -> TransactionType.BALANCE
                    bodyLowerCase.contains(Regex("(.*) give (.*)")) -> TransactionType.DEPOSIT
                    else -> TransactionType.UNKNOWN
                }

                val amount = body.split("Ksh")[1].split(" ")[0].replace(",", "").toDouble()

                val accountNumber = when (transactionType) {
                    TransactionType.REVERSAL -> null
                    TransactionType.SEND -> body.split("to ")[1].split(" on")[0]
                    TransactionType.PAY_BILL -> body.split("to ")[1].split(" on")[0]
                    TransactionType.BUY_GOODS -> body.split("to ")[1].split(" on")[0]
                    TransactionType.WITHDRAW -> body.split("from ")[1].split(" new")[0]
                    TransactionType.RECEIVE -> body.split("from ")[1].split(" on")[0]
                    TransactionType.AIRTIME -> null
                    TransactionType.BALANCE -> null
                    TransactionType.DEPOSIT -> bodyLowerCase.split("to ")[1].split(" new")[0]
                    TransactionType.UNKNOWN -> null
                }

                val transationDate = when (transactionType) {
                    TransactionType.REVERSAL -> DateTime.parse(
                        "d/M/yy  h:mm a",
                        bodyLowerCase.split(" on ")[1].split(" and")[0].replace("at", "")
                    ).timestamp
                    TransactionType.SEND -> DateTime.parse(
                        "d/M/yy  h:mm a",
                        bodyLowerCase.split(" on ")[1].split(".")[0].replace("at", "")
                    ).timestamp
                    TransactionType.PAY_BILL -> DateTime.parse(
                        "d/M/yy  h:mm a",
                        bodyLowerCase.split(" on ")[1].split(" new")[0].replace("at", "")
                    ).timestamp
                    TransactionType.BUY_GOODS -> DateTime.parse(
                        "d/M/yy  h:mm a",
                        bodyLowerCase.split(" on ")[1].split(".")[0].replace("at", "")
                    ).timestamp
                    TransactionType.WITHDRAW -> DateTime.parse(
                        "d/M/yy  h:mm a",
                        bodyLowerCase.split("on ")[1].split("withdraw")[0].replace("at", "")
                    ).timestamp
                    TransactionType.RECEIVE -> DateTime.parse(
                        "d/M/yy  h:mm a",
                        bodyLowerCase.split(" on ")[1].split(".")[0].replace("at", "")
                    ).timestamp
                    TransactionType.AIRTIME -> DateTime.parse(
                        "d/M/yy  h:mm a",
                        bodyLowerCase.split(" on ")[1].split(".")[0].replace("at", "")
                    ).timestamp
                    TransactionType.BALANCE -> DateTime.parse(
                        "d/M/yy  h:mm a",
                        bodyLowerCase.split(" on ")[1].split(".")[0].replace("at", "")
                    ).timestamp
                    TransactionType.DEPOSIT -> DateTime.parse(
                        "d/M/yy  h:mm a",
                        bodyLowerCase.split(" on ")[1].split(" give")[0].replace("at", "")
                    ).timestamp
                    TransactionType.UNKNOWN -> 0
                }

                val balance = when (transactionType) {
                    TransactionType.REVERSAL -> body.split("balance is Ksh")[1].dropLast(1).replace(",", "").toDouble()
                    TransactionType.SEND -> body.split("balance is Ksh")[1].split(". Transaction cost")[0].replace(
                        ",",
                        ""
                    ).toDouble()
                    TransactionType.PAY_BILL -> body.split("balance is Ksh")[1].split(". Transaction cost")[0].replace(
                        ",",
                        ""
                    ).toDouble()
                    TransactionType.BUY_GOODS -> body.split("balance is Ksh")[1].split(". Transaction cost")[0].replace(
                        ",",
                        ""
                    ).toDouble()
                    TransactionType.WITHDRAW -> body.split("balance is Ksh")[1].split(". Transaction cost")[0].replace(
                        ",",
                        ""
                    ).toDouble()
                    TransactionType.RECEIVE -> body.split("balance is Ksh")[1].split(". ")[0].replace(
                        ",",
                        ""
                    ).toDouble()
                    TransactionType.AIRTIME -> body.split("balance is Ksh")[1].split(". Transaction cost")[0].replace(
                        ",",
                        ""
                    ).toDouble()
                    TransactionType.BALANCE -> body.split("balance was  Ksh")[1].split("  on")[0].replace(
                        ",",
                        ""
                    ).toDouble()
                    TransactionType.DEPOSIT -> body.split("balance is Ksh")[1].replace(",", "").toDouble()
                    TransactionType.UNKNOWN -> 0.0
                }

                val transactionCost = when (transactionType) {
                    TransactionType.REVERSAL -> 0.0
                    TransactionType.SEND -> body.split("Transaction cost, Ksh")[1].split(".")[0].replace(
                        ",",
                        ""
                    ).toDouble()
                    TransactionType.PAY_BILL -> body.split("Transaction cost, Ksh")[1].dropLast(1).replace(
                        ",",
                        ""
                    ).toDouble()
                    TransactionType.BUY_GOODS -> body.split("Transaction cost, Ksh")[1].dropLast(1).replace(
                        ",",
                        ""
                    ).toDouble()
                    TransactionType.WITHDRAW -> body.split("Transaction cost, Ksh")[1].dropLast(1).replace(
                        ",",
                        ""
                    ).toDouble()
                    TransactionType.RECEIVE -> 0.0
                    TransactionType.AIRTIME -> body.split("Transaction cost, Ksh")[1].split(".")[0].replace(
                        ",",
                        ""
                    ).toDouble()
                    TransactionType.BALANCE -> 0.0
                    TransactionType.DEPOSIT -> 0.0
                    TransactionType.UNKNOWN -> 0.0
                }

                return MpesaMessage(
                    body,
                    code,
                    transactionType,
                    amount,
                    accountNumber,
                    transationDate,
                    balance,
                    transactionCost
                )
            } catch (e: Exception) {
                FirebaseCrashlytics.getInstance().log(body)
                throw e
            }
        }
    }

}