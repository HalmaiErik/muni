import AddIcon from '@mui/icons-material/Add';
import { Card, CardContent, IconButton, LinearProgress, Paper, Typography } from "@mui/material";
import { DatePicker, LocalizationProvider } from "@mui/x-date-pickers";
import { AdapterDayjs } from "@mui/x-date-pickers/AdapterDayjs";
import dayjs, { Dayjs } from "dayjs";
import { useEffect, useState } from "react";
import { useNavigate, useSearchParams } from "react-router-dom";
import { useCreateCustomer, useCustomerCategories, useCustomerInfo, useCustomerStats } from "../../api/bank-account-data-api";
import AccountsCarousel from "../../components/accounts-carousel/AccountsCarousel";
import Auth from "../../components/auth/Auth";
import BankInstitutionSelection from "../../components/bank-institution-selection/BankInstitutionSelection";
import CategoryList from "../../components/category-list/CategoryList";
import ChartList from "../../components/chart-list/ChartList";
import TransactionsTable from "../../components/transactions-table/TransactionsTable";
import { useAuth } from "../../contexts/AuthContext";
import { LOCAL_STORAGE_INSTITUTION_LOGO, LOCAL_STORAGE_INSTITUTION_NAME } from "../../utils/constants";

const Home = () => {
    const today = new Date();
    const firstOfThisMonth = new Date(today.getFullYear(), today.getMonth(), 1);
    const [fromDate, setFromDate] = useState<Dayjs>(dayjs(firstOfThisMonth));
    const [toDate, setToDate] = useState<Dayjs>(dayjs(today));
    const [wantToAdd, setWantToAdd] = useState(false);

    const { currentUser, isAuthenticated } = useAuth();
    const [searchParams] = useSearchParams();
    const requisitionId = searchParams.get("ref");
    const { data: customerInfo, isLoading: customerInfoLoading, isRefetching: customerInfoRefetching } = useCustomerInfo(currentUser);
    const { data: categories, isLoading: categoriesLoading, isRefetching: categoriesRefetching } = useCustomerCategories(currentUser);
    const { data: stats, isLoading: statsLoading, isRefetching: statsRefetching } = useCustomerStats(currentUser, { from: fromDate.toDate(), to: toDate.toDate() });
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

    const changeFromDate = (val: Dayjs | null) => {
        if (val) {
            setFromDate(val);
        }
    };

    const changeToDate = (val: Dayjs | null) => {
        if (val) {
            setToDate(val);
        }
    };

    if (customerInfoLoading || customerInfoRefetching
        || categoriesLoading || categoriesRefetching
        || statsLoading || statsRefetching) {
        return (
            <div style={{ maxWidth: 1256, margin: 'auto' }}>
                <LinearProgress />
            </div>
        );
    }

    if (wantToAdd) {
        return (
            <div style={{ maxWidth: '1256px', margin: 'auto' }}>
                <BankInstitutionSelection onClose={() => setWantToAdd(false)} />
            </div>
        );
    }

    return (
        <>
            {!isAuthenticated && (
                <Paper sx={{ maxWidth: '512px', margin: 'auto', padding: '32px' }}>
                    <Auth />
                </Paper>
            )}
            {customerInfo && customerInfo.accounts.length === 0 && (
                <Paper sx={{ maxWidth: '512px', margin: 'auto', padding: '32px' }}>
                    {currentUser && <BankInstitutionSelection />}
                </Paper>
            )}
            {customerInfo && customerInfo.accounts.length !== 0 && (
                <div style={{ maxWidth: '1256px', margin: 'auto' }}>
                    <div style={{ display: 'flex', marginBottom: '8px' }}>
                        <Typography sx={{ flexGrow: 1 }} variant="h3">Accounts</Typography>

                        <IconButton size="large" onClick={() => setWantToAdd(true)}>
                            <AddIcon fontSize="inherit" />
                        </IconButton>
                    </div>
                    <div style={{ marginBottom: '32px', outlineStyle: 'solid', outlineWidth: '2px', outlineColor: '#1E1E1E' }}>
                        <AccountsCarousel accounts={customerInfo.accounts} />
                    </div>

                    <div style={{ marginBottom: '32px' }}>
                        {categories && <CategoryList categories={categories} />}
                    </div>

                    {customerInfo.transactions.length !== 0 && stats && (
                        <Card sx={{ marginBottom: '32px' }}>
                            <CardContent>
                                <LocalizationProvider dateAdapter={AdapterDayjs}>
                                    <div style={{ display: 'flex', marginBottom: '32px' }}>
                                        <DatePicker
                                            sx={{ width: '192px', marginRight: '8px' }}
                                            label="From"
                                            slotProps={{ textField: { size: 'small' } }}
                                            onChange={changeFromDate}
                                            value={fromDate}
                                            disableFuture
                                        />
                                        <DatePicker
                                            sx={{ width: '192px', marginRight: '8px' }}
                                            label="To"
                                            slotProps={{ textField: { size: 'small' } }}
                                            onChange={changeToDate}
                                            value={toDate}
                                        />
                                    </div>
                                </LocalizationProvider>

                                <ChartList stats={stats} />
                            </CardContent>
                        </Card>
                    )}
                    {customerInfo.transactions.length !== 0 && categories && (
                        <Card variant="outlined">
                            <TransactionsTable transactions={customerInfo.transactions} categories={categories} />
                        </Card>
                    )}
                </div>
            )}
        </>
    );
};

export default Home;