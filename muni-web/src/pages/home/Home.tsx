import { Paper, Stack } from "@mui/material";
import Auth from "../../components/auth/Auth"
import { useAuth } from "../../contexts/AuthContext";
import styles from "./Home.module.css"
import BankInstitutionSelection from "../../components/bank-institution-selection/BankInstitutionSelection";
import { useEffect, useState } from "react";
import { AccountDto } from "../../api/dtos";
import { createCustomer, getCustomerAccounts } from "../../api/bank-account-data-api";
import { useSearchParams } from "react-router-dom";
import AccountsList from "../../components/accounts-list/AccountsList";
import { LOCAL_STORAGE_INSTITUTION_LOGO, LOCAL_STORAGE_INSTITUTION_NAME } from "../../utils/constants";

const Home = () => {
    const [accounts, setAccounts] = useState<AccountDto[]>([]);
    const { currentUser } = useAuth();
    const [searchParams] = useSearchParams();
    const requisitionId = searchParams.get("ref");

    useEffect(() => {
        getAccounts();
    }, [currentUser]);

    useEffect(() => {
        const institutionName = window.localStorage.getItem(LOCAL_STORAGE_INSTITUTION_NAME);
        const institutionLogo = window.localStorage.getItem(LOCAL_STORAGE_INSTITUTION_LOGO);
        if (requisitionId && currentUser && institutionName && institutionLogo) {
            window.localStorage.removeItem(LOCAL_STORAGE_INSTITUTION_NAME);
            window.localStorage.removeItem(LOCAL_STORAGE_INSTITUTION_LOGO);

            createCustomer({ requisitionId, institutionName, institutionLogo })
                .then(() => getAccounts());
        }
    }, []);

    const getAccounts = () => {
        if (currentUser) {
            getCustomerAccounts()
                .then((data) => {
                    setAccounts(data);
                });
        }
    };

    return (
        <>
            {accounts.length === 0 && (
                <Paper
                    sx={{
                        maxWidth: '512px',
                        margin: 'auto',
                        padding: '32px'
                    }}
                >
                    {!currentUser && <Auth />}
                    {currentUser && <BankInstitutionSelection />}
                </Paper>
            )}
            {accounts.length !== 0 && (
                <AccountsList accounts={accounts} />
            )}
        </>
    );
};

export default Home;