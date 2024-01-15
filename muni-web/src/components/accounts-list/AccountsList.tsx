import { Card, CardActionArea, Paper, Stack, Typography } from "@mui/material";
import { AccountDetailsDto } from "../../api/dtos";
import styles from "./AccountsList.module.css"
import { useNavigate } from "react-router-dom";

type Props = {
    accounts: AccountDetailsDto[];
};

const AccountsList = ({ accounts }: Props) => {
    const navigate = useNavigate();

    const onSelectAccount = (accountId: string) => {
        navigate(`/${accountId}`);
    };

    return (
        <div>
            {accounts.map((account, index) =>
                <div key={index} className={styles.container}>
                    <Card>
                        <CardActionArea className={styles.card} onClick={() => onSelectAccount(account.resourceId)}>
                            <Stack>
                                <Typography variant="button" gutterBottom>
                                    {account.name}
                                </Typography>
                                <Typography variant="overline" gutterBottom>
                                    {account.iban}
                                </Typography>
                            </Stack>
                        </CardActionArea>
                    </Card>
                </div>
            )}
        </div>
    );
};

export default AccountsList;