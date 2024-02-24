import { Paper } from "@mui/material";
import { useEffect } from "react";
import { useNavigate, useSearchParams } from "react-router-dom";
import { useCreateCustomer, useCustomerAccounts } from "../../api/bank-account-data-api";
import AccountsCarousel from "../../components/accounts-carousel/AccountsCarousel";
import Auth from "../../components/auth/Auth";
import BankInstitutionSelection from "../../components/bank-institution-selection/BankInstitutionSelection";
import { useAuth } from "../../contexts/AuthContext";
import { LOCAL_STORAGE_INSTITUTION_LOGO, LOCAL_STORAGE_INSTITUTION_NAME } from "../../utils/constants";

const Home = () => {
    const { currentUser, isAuthenticated } = useAuth();
    const [searchParams] = useSearchParams();
    const requisitionId = searchParams.get("ref");
    const { data: accounts } = useCustomerAccounts(currentUser);
    const { mutate: createCustomer } = useCreateCustomer();
    const navigate = useNavigate();

    useEffect(() => {
        const institutionName = window.localStorage.getItem(LOCAL_STORAGE_INSTITUTION_NAME);
        const institutionLogo = window.localStorage.getItem(LOCAL_STORAGE_INSTITUTION_LOGO);
        if (requisitionId && currentUser && institutionName && institutionLogo) {
            window.localStorage.removeItem(LOCAL_STORAGE_INSTITUTION_NAME);
            window.localStorage.removeItem(LOCAL_STORAGE_INSTITUTION_LOGO);

            createCustomer({ requisitionId, institutionName, institutionLogo })
            navigate('/');
        }
    }, []);

    return (
        <>
            {!isAuthenticated && (
                <Paper sx={{ maxWidth: '512px', margin: 'auto', padding: '32px' }}>
                    <Auth />
                </Paper>
            )}
            {accounts && accounts.length === 0 && (
                <Paper sx={{ maxWidth: '512px', margin: 'auto', padding: '32px' }}>
                    {currentUser && <BankInstitutionSelection />}
                </Paper>
            )}
            {accounts && accounts.length !== 0 && (
                <div style={{ maxWidth: '1256px', margin: 'auto' }}>
                    <AccountsCarousel accounts={accounts} />
                </div>
            )}
        </>
    );
};

export default Home;