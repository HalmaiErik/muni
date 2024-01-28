import { useEffect, useState } from "react";
import { TransactionDto } from "../../api/dtos";
import { useParams } from "react-router-dom";
import { useAuth } from "../../contexts/AuthContext";
import { getAccountTransactions } from "../../api/bank-account-data-api";
import TransactionsTable from "../../components/transactions-table/TransactionsTable";
import { Paper } from "@mui/material";

const Account = () => {
    const [transactions, setTransactions] = useState<TransactionDto[]>();
    const { accountExternalId } = useParams();
    const { currentUser } = useAuth();

    useEffect(() => {
        if (accountExternalId && currentUser && currentUser.email) {
            getAccountTransactions({ email: currentUser.email, accountExternalId, refresh: false })
                .then((data) => {
                    console.log(data);
                    setTransactions(data);
                })
                .catch();
        }
    }, []);

    return (
        <>
            {transactions && (
                <Paper sx={{ width: 1256, height: 512, margin: 'auto' }}>
                    <TransactionsTable transactions={transactions} />
                </Paper>
            )}
        </>
    );
};

export default Account;