import { Paper, Stack } from "@mui/material";
import Auth from "../../components/auth/Auth"
import { useAuth } from "../../contexts/AuthContext";
import styles from "./Home.module.css"
import LogoutButton from "../../components/auth/LogoutButton";
import BankInstitutionSelection from "../bank-institution-selection/BankInstitutionSelection";
import { useEffect, useState } from "react";
import { AccountDetailsDto } from "../../api/dtos";
import { createCustomer, getCustomerAccounts } from "../../api/bank-account-data-api";
import { useSearchParams } from "react-router-dom";
import AccountsList from "../../components/accounts-list/AccountsList";

const Home = () => {
    const [accounts, setAccounts] = useState<AccountDetailsDto[]>();
    const { currentUser } = useAuth();
    const [searchParams] = useSearchParams();
    const requisitionId = searchParams.get("ref");

    useEffect(() => {
        getAccounts();
    }, [currentUser]);

    useEffect(() => {
        if (requisitionId && currentUser && currentUser.email) {
            createCustomer({ email: currentUser.email, requisitionId })
                .then(() => getAccounts());
        }
    }, []);


    const getAccounts = () => {
        if (currentUser && currentUser.email) {
            getCustomerAccounts(currentUser.email)
                .then((data) => {
                    console.log(data);
                    setAccounts(data);
                }
                );
        }
    };

    return (
        <>
            {accounts && accounts.length === 0 && (
                <Paper className={styles.authPaper}>
                    {!currentUser && <Auth />}
                    {currentUser && accounts && accounts.length === 0 && <BankInstitutionSelection saveCustomer={false} />}
                    {currentUser && accounts && accounts.length !== 0 && JSON.stringify(accounts)}
                </Paper>
            )}
            {accounts && accounts.length !== 0 && (
                <AccountsList accounts={accounts} />
            )}
        </>
    );
};

export default Home;